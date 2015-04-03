package net.sourceforge.entrainer.gui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;

public class EntrainerFXResizer {

	private enum Corner {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
	}

	private enum Side {
		TOP, RIGHT, BOTTOM, LEFT;
	}

	private ResizerListener listener;
	private Corner corner;
	private Rectangle2D size;

	private boolean dragStarting;
	private boolean resize;

	private Lock lock = new ReentrantLock();

	private double screenX;
	private double screenY;

	public EntrainerFXResizer(ResizerListener listener) {
		this.listener = listener;
	}

	public void onRelease(MouseEvent e) {
		dragStarting = false;
		resize = false;
	}

	public void onDrag(MouseEvent e) {
		lock.lock();
		try {
			if (dragStarting) {
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
		resize = e.isMetaDown();
		setCorner(e);
		dragStarting = true;
	}

	private void reposition(MouseEvent e) {
		double minX = size.getMinX() + (e.getScreenX() - screenX);
		double minY = size.getMinY() + (e.getScreenY() - screenY);
		
		size = new Rectangle2D(minX, minY, size.getWidth(), size.getHeight());
		
		listener.resize(size);
	}

	private void resize(MouseEvent e) {
		switch(corner) {
		case BOTTOM_LEFT:
			resizeBottomLeft(e);
			break;
		case BOTTOM_RIGHT:
			resizeBottomRight(e);
			break;
		case TOP_LEFT:
			resizeTopLeft(e);
			break;
		case TOP_RIGHT:
			resizeTopRight(e);
			break;
		default:
			break;
		
		}
		
		listener.resize(size);
	}

	private void resizeTopRight(MouseEvent e) {
		double width = size.getWidth() + (screenX - e.getScreenX());
		double height = size.getHeight() + (screenY - e.getScreenY());
		double minX = size.getMinX();
		double minY = size.getMinY() + (screenY - e.getScreenY());
		
		size = new Rectangle2D(minX, minY, width, height);
	}

	private void resizeTopLeft(MouseEvent e) {
		double width = size.getWidth() + (e.getScreenX()- screenX);
		double height = size.getHeight() + (screenY - e.getScreenY());
		double minX = size.getMinX() + (screenX - e.getScreenX());
		double minY = size.getMinY() + (screenY - e.getScreenY());
		
		size = new Rectangle2D(minX, minY, width, height);
	}

	private void resizeBottomRight(MouseEvent e) {
		double width = size.getWidth() + (screenX - e.getScreenX());
		double height = size.getHeight() + (e.getScreenY() - screenY);
		double minX = size.getMinX();
		double minY = size.getMinY();
		
		size = new Rectangle2D(minX, minY, width, height);
	}

	private void resizeBottomLeft(MouseEvent e) {
		double width = size.getWidth() + (e.getScreenX()- screenX);
		double height = size.getHeight() + (e.getScreenY() - screenY);
		double minX = size.getMinX();
		double minY = size.getMinY();
		
		size = new Rectangle2D(minX, minY, width, height);
	}

	private void setCorner(MouseEvent e) {
		double leftToMouse = e.getX() - size.getMinX();
		double mouseToRight = size.getMaxX() - e.getX();

		Side leftRight = leftToMouse <= mouseToRight ? Side.LEFT : Side.RIGHT;

		double topToMouse = e.getY() - size.getMinY();
		double mouseToBottom = size.getMaxY() - e.getY();

		Side topBottom = topToMouse <= mouseToBottom ? Side.TOP : Side.BOTTOM;

		switch (topBottom) {
		case TOP:
			switch (leftRight) {
			case LEFT:
				corner = Corner.TOP_LEFT;
				break;
			case RIGHT:
				corner = Corner.TOP_RIGHT;
				break;
			default:
				break;
			}
		case BOTTOM:
			switch (leftRight) {
			case LEFT:
				corner = Corner.BOTTOM_LEFT;
				break;
			case RIGHT:
				corner = Corner.BOTTOM_RIGHT;
				break;
			default:
				break;
			}
		default:
			break;
		}
	}

	public void setSize(Rectangle2D size) {
		this.size = size;
	}

	public interface ResizerListener {
		void resize(Rectangle2D size);
	}

}
