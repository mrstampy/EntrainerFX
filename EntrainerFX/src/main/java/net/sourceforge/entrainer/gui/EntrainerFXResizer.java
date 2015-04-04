package net.sourceforge.entrainer.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;

public class EntrainerFXResizer {

	private ResizerListener listener;
	private Rectangle2D size;

	private boolean dragStarted;
	private boolean resize;

	private Lock lock = new ReentrantLock();

	private double screenX;
	private double screenY;
	
	private ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
	private boolean clicked;
	private Future<?> future;

	public EntrainerFXResizer(ResizerListener listener) {
		this.listener = listener;
	}

	public void onRelease(MouseEvent e) {
		dragStarted = false;
		resize = false;
	}
	
	public void onClick(MouseEvent e) {
		clicked = true;
		if(future != null) future.cancel(true);
		
		future = svc.schedule(() -> clicked = false, 500, TimeUnit.MILLISECONDS);
	}

	public void onDrag(MouseEvent e) {
		lock.lock();
		try {
			if (dragStarted) {
				doDrag(e);
			} else {
				initDrag(e);
			}
			screenX = e.getScreenX();
			screenY = e.getScreenY();
		} finally {
			lock.unlock();
		}
	}

	private void doDrag(MouseEvent e) {
		if (resize) {
			resize(e);
		} else {
			reposition(e);
		}
	}

	private void initDrag(MouseEvent e) {
		if(future != null) future.cancel(true);
		resize = clicked;
		dragStarted = true;
		clicked = false;
	}

	private void reposition(MouseEvent e) {
		double minX = size.getMinX() + (e.getScreenX() - screenX);
		double minY = size.getMinY() + (e.getScreenY() - screenY);
		
		size = new Rectangle2D(minX, minY, size.getWidth(), size.getHeight());
		
		listener.resize(size);
	}

	private void resize(MouseEvent e) {
		double width = size.getWidth() + (e.getScreenX() - screenX);
		double height = size.getHeight() + (e.getScreenY() - screenY);
		
		size = new Rectangle2D(size.getMinX(), size.getMinY(), width, height);
		
		listener.resize(size);
	}

	public void setSize(Rectangle2D size) {
		this.size = size;
	}
	
	public Rectangle2D getSize() {
		return size;
	}

	public interface ResizerListener {
		void resize(Rectangle2D size);
	}

}
