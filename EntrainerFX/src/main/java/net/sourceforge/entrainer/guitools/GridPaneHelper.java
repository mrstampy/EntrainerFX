package net.sourceforge.entrainer.guitools;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Making the use of GridPanes less paneful.
 * @author burton
 *
 */
public class GridPaneHelper {
	
	private GridPane pane;
	
	private List<Node> nodes = new ArrayList<>();
	
	private int row;
	private int column;

	public GridPaneHelper() {
		this(new GridPane());
	}
	
	public GridPaneHelper(GridPane pane) {
		this.pane = pane;
	}
	
	public GridPaneHelper skip() {
		return skip(1);
	}
	
	public GridPaneHelper skip(int num) {
		column += num;
		return this;
	}
	
	public GridPaneHelper newLine() {
		return newLine(1);
	}
	
	public GridPaneHelper newLine(int num) {
		row += num;
		column = 0;
		
		return this;
	}
	
	public GridPaneHelper add(String label) {
		return add(new Label(label));
	}
	
	public GridPaneHelper add(String label, int width, int height) {
		return add(new Label(label), width, height);
	}
	
	public GridPaneHelper addLast(String label) {
		return addLast(new Label(label));
	}
	
	public GridPaneHelper addLast(String label, int width, int height) {
		return addLast(new Label(label), width, height);
	}
	
	public GridPaneHelper addLast(Node node) {
		return addLast(node, 1, 1);
	}
	
	public GridPaneHelper addLast(Node node, int width, int height) {
		add(node, width, height);
		
		newLine();
		
		return this;
	}
	
	public GridPaneHelper add(Node node) {
		return add(node, 1, 1);
	}
	
	public GridPaneHelper add(Node node, int width, int height) {
		GridPane.setConstraints(node, column, row, width, height);
		
		skip(width);
		
		nodes.add(node);
		
		return this;
	}
	
	public GridPaneHelper hGrow(Node node, Priority priority) {
		GridPane.setHgrow(node, priority);
		
		return this;
	}
	
	public GridPaneHelper vGrow(Node node, Priority priority) {
		GridPane.setVgrow(node, priority);
		
		return this;
	}
	
	public GridPaneHelper hAlignment(Node node, HPos pos) {
		GridPane.setHalignment(node, pos);
		
		return this;
	}
	
	public GridPaneHelper vAlignment(Node node, VPos pos) {
		GridPane.setValignment(node, pos);
		
		return this;
	}
	
	public GridPaneHelper fillHeight(Node node, Boolean fill) {
		GridPane.setFillHeight(node, fill);
		
		return this;
	}
	
	public GridPaneHelper fillWidth(Node node, Boolean fill) {
		GridPane.setFillWidth(node, fill);
		
		return this;
	}
	
	public GridPaneHelper reset() {
		nodes.clear();
		this.pane = new GridPane();
		row = 0;
		column = 0;
		
		return this;
	}
	
	public GridPaneHelper alignment(Pos pos) {
		pane.setAlignment(pos);
		
		return this;
	}
	
	public GridPaneHelper gridLines(boolean b) {
		pane.setGridLinesVisible(b);
		
		return this;
	}
	
	public GridPaneHelper hGap(double gap) {
		pane.setHgap(gap);
		
		return this;
	}
	
	public GridPaneHelper vGap(double gap) {
		pane.setVgap(gap);
		
		return this;
	}

	public GridPane getPane() {
		if(pane.getChildren().isEmpty()) pane.getChildren().addAll(nodes);
		
		return pane;
	}

}
