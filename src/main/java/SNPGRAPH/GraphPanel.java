package SNPGRAPH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {

	@Serial
	private static final long serialVersionUID = 1L;

	private final int WIDTH = 700;
	private final int HEIGHT = 600;

	private double[] up_values, down_values;
	int graphWindowSize, totalFrags;

	/**
	 * Constructor for the GraphPanel object setting all relevant variables
	 */

	public GraphPanel(double[] up_values, double[] down_values,
			int graphWindowSize, int totalFrags) {
		this.setBackground(Color.WHITE);

		this.graphWindowSize = graphWindowSize;
		this.totalFrags = totalFrags;
		this.up_values = new double[graphWindowSize];
		this.down_values = new double[graphWindowSize];

		for (int i = 0; i < graphWindowSize; i++) {

			if (i > up_values.length - 1) {
				this.up_values[i] = 0.0;
			} else {
				this.up_values[i] = up_values[i];
			}

			if (i > down_values.length - 1) {
				this.up_values[i] = 0.0;
			} else {
				this.down_values[i] = down_values[i];
			}
		}
	}

	/**
	 * Overwriting the JComponent method paintComponent to be able to draw the
	 * graph
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		double height = getHeight();
		double width = getWidth();

		// Set scaling for labeling and graph points (values) for x axis
		double PADDING = 55.0;
		double xScale = (width - 2 * PADDING) / 8.0;
		double xValueScaling = xScale / (graphWindowSize / 4.0);

		// Set scaling for labeling and graph points (values) for y axis
		double yScale = (height - 2 * PADDING) / 4.0;
		double maxProbValue = getMaxValue(up_values, down_values)
				/ ((double) totalFrags);
		double yLabelValueScale = maxProbValue / 4.0;
		double yValuesScaling = ((height - 2 * PADDING) / maxProbValue)
				/ (double) totalFrags;

		// Draw line for x axis
		g2.draw(new Line2D.Double(PADDING, (height - PADDING),
				(width - PADDING), (height - PADDING)));

		// Draw line for y axis
		g2.draw(new Line2D.Double(PADDING, height - PADDING, PADDING, PADDING));

		// Draw grid for x axis
		for (double i = 0.0; i < 9; i++) {
			if (i % 2 == 0) {
				g2.draw(new Line2D.Double(xScale * i + PADDING, height
						- PADDING + 10.0, xScale * i + PADDING, height
						- PADDING - 10.0));
			} else {
				g2.draw(new Line2D.Double(xScale * i + PADDING, height
						- PADDING + 5.0, xScale * i + PADDING, height - PADDING
						- 5.0));
			}
		}

		// Draw grid for y axis
		for (double i = 0.0; i < 5; i++) {
			if (i % 2 == 0) {
				g2.draw(new Line2D.Double(PADDING - 10.0, height - PADDING
						- yScale * i, PADDING + 10.0, height - PADDING - yScale
						* i));
			} else {
				g2.draw(new Line2D.Double(PADDING - 5.0, height - PADDING
						- yScale * i, PADDING + 5.0, height - PADDING - yScale
						* i));
			}
		}

		// Label x axis
		g2.drawString("0", (float) (getWidth() / 2.0 - 4.0),
				(float) (getHeight() - 18));
		int xLabel = (graphWindowSize / 4);
		for (double i = 1; i < 5.0; i++) {
			String label = Integer.toString(xLabel);
			g2.drawString(label, (float) (width / 2.0 + xScale * i - 13.0),
					(float) (getHeight() - 18));
			g2.drawString(label, (float) (width / 2.0 - xScale * i - 13.0),
					(float) (getHeight() - 18));
			xLabel += (graphWindowSize / 4);
		}

		// Label y axis
		for (double i = 0; i < 5; i++) {
			String label = Double.toString(SNPGRAPH.round(
					(i * yLabelValueScale), 4));
			g2.drawString(label, (float) 0.0, (float) (height - PADDING
					- yScale * i + 5.0));
		}

		// Draw a line from peak to next position upstream and downstream
		g2.draw(new Line2D.Double(width / 2.0, height - PADDING
				- (this.up_values[0] + this.down_values[0]) * yValuesScaling,
				width / 2.0 + xValueScaling, height - PADDING
						- this.up_values[1] * yValuesScaling));

		g2.draw(new Line2D.Double(width / 2.0, height - PADDING
				- (this.up_values[0] + this.down_values[0]) * yValuesScaling,
				width / 2.0 - xValueScaling, height - PADDING
						- this.down_values[1] * yValuesScaling));

		// Draw upstream SNP distribution
		for (int i = 1; i < this.up_values.length - 1; i++) {
			g2.draw(new Line2D.Double(width / 2.0 + i * xValueScaling, height
					- PADDING - this.up_values[i] * yValuesScaling, width / 2.0
					+ (i + 1.0) * xValueScaling, height - PADDING
					- this.up_values[i + 1] * yValuesScaling));
		}

		// Draw downstream SNP distribution
		for (int i = 1; i < this.down_values.length - 1; i++) {
			g2.draw(new Line2D.Double(width / 2.0 - i * xValueScaling, height
					- PADDING - this.down_values[i] * yValuesScaling, width
					/ 2.0 - (i + 1.0) * xValueScaling, height - PADDING
					- this.down_values[i + 1] * yValuesScaling));
		}
	}

	/**
	 * Return count of the most counted SNP
	 */

	public double getMaxValue(double[] up_values, double[] down_values) {
		double maxUpValue = 0, maxDownValue = 0, maxValue;
		for (double up_value : up_values) {
			if (up_value > maxUpValue) {
				maxUpValue = up_value;
			}
		}

		for (double down_value : down_values) {
			if (down_value > maxDownValue) {
				maxDownValue = down_value;
			}
		}
		maxValue = Math.max(maxUpValue, maxDownValue);
		return maxValue;
	}

	/**
	 * Export the JPanel to a PNG file
	 */
	public void exportGraph(String file_name) {
		BufferedImage bi = new BufferedImage(this.getSize().width,
				this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		this.paint(g);
		g.dispose();
		String file = file_name.replaceFirst("[.][^.]+$", "");
		File directory = new File("graph/");
		if (! directory.exists()){
			directory.mkdir();
		}
		try {
			ImageIO.write(bi, "png", new File("graph/"+file+".png"));
			System.out.println(file+".png saved in folder graph!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Visualization could not be saved to output folder!");
		}
	}

	/**
	 * Starting the visualisation process
	 */
	//closeAll
	public static void showGUI(double[] up_values, double[] down_values,
			int graphWindowSize, int totalFrags, String file_name) {
			
		GraphPanel panel = new GraphPanel(up_values, down_values,
				graphWindowSize, totalFrags);
		panel.setPreferredSize(new Dimension(panel.WIDTH, panel.HEIGHT));

		JFrame frame = new JFrame("SNPGRAPH");
//		TODO: close all windows or every window separately depending on flag
//		if(closeAll=="y"){
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		}
		JButton exit = new JButton("EXIT");
		exit.addActionListener(e -> System.exit(0));
		frame.setMinimumSize(new Dimension(275, 275));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(exit, BorderLayout.NORTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		panel.exportGraph(file_name);
	}
}
