package srtfromtxt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Preview extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -552221761511237665L;
	JPanel panel = new JPanel();
	JSlider slider = new JSlider();
	JLabel maxLengthLbl = new JLabel("");
	JTextPane prevTp = new JTextPane();

	private final JPanel panel_1 = new JPanel();
	private final JButton prevCreateSrtButt = new JButton("Create SRT");
	private final JPanel panel_2 = new JPanel();

	public Preview() throws IOException {
		//reset the sub count
		Sub.setIdSub(0);
		
		setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		panel_1.setPreferredSize(new Dimension(200, 50));

		panel.add(panel_1, BorderLayout.WEST);

		JLabel lblNewLabel_2 = new JLabel("Adjust sub max. length: ");
		panel_1.add(lblNewLabel_2);
		panel_1.add(maxLengthLbl);
		panel_1.add(slider);
		slider.setFont(new Font("Tahoma", Font.PLAIN, 8));
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(10);
		slider.setMinimum(20);
		slider.setMaximum(80);
		slider.setValue(40);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		maxLengthLbl.setText(String.valueOf(Txt2Srt.getCharMax()));

		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		prevCreateSrtButt.setAlignmentX(Component.CENTER_ALIGNMENT);
		prevCreateSrtButt.setHorizontalTextPosition(SwingConstants.CENTER);
		panel_2.add(prevCreateSrtButt);

		JScrollPane scrollPane = new JScrollPane(prevTp);
		add(scrollPane, BorderLayout.CENTER);

		prevTp.setText(Txt2Srt.makeOutStr());

		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				update();
			}
		});
		slider.setPaintLabels(true);

		prevCreateSrtButt.addActionListener(new ActionListener() {
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent arg0) {
				File srtFile = new File(Txt2Srt.getDestination() + "/"
						+ Txt2Srt.getFile2Convert().getName().replaceFirst(".txt", "") + ".srt");
				if (srtFile.exists()) {
					int rep = new JOptionPane().showConfirmDialog(null, "File already exist, Overwrite ?", "Warning",
							JOptionPane.OK_CANCEL_OPTION);
					if (rep == JOptionPane.OK_OPTION) {
						if (srtFile.delete()) {
							try {
								Txt2Srt.makeFile(prevTp.getText());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							new JOptionPane().showMessageDialog(null, "Can't Overwrite. Sorry dude", "Warning",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					try {
						Txt2Srt.makeFile(prevTp.getText());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void update() {
		maxLengthLbl.setText(String.valueOf(slider.getValue()));
		Txt2Srt.setCharMax(slider.getValue());
		Sub.setIdSub(0);
		try {
			prevTp.setText(Txt2Srt.makeOutStr());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
