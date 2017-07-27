package com.ben.srtcreator.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ben.srtcreator.Txt2Srt;

import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

public class MainPnl extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3918570445594063881L;
	private JTextField maxCharTf;

	/**
	 * Create the panel.
	 */
	public MainPnl() {
		setLayout(new BorderLayout(0, 0));

		JPanel selectFilePnl = new JPanel();
		FlowLayout flowLayout = (FlowLayout) selectFilePnl.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(selectFilePnl, BorderLayout.CENTER);

		JFileChooser inputJfc = new JFileChooser();
		inputJfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		inputJfc.setFileFilter(new FileNameExtensionFilter("*.txt filter", "txt"));

		JFileChooser outputJfc = new JFileChooser();
		outputJfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(435, 25));
		selectFilePnl.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblSelectFileIn = new JLabel("Input: ");
		panel_1.add(lblSelectFileIn, BorderLayout.WEST);

		JLabel lblSelectedFileUrl = new JLabel("(Ex. D:\\THISPROJECT\\ClientSources\\VoiceOverV1.txt)");
		panel_1.add(lblSelectedFileUrl);
		lblSelectedFileUrl.setEnabled(false);

		JButton selectInputButt = new JButton("Browse");
		panel_1.add(selectInputButt, BorderLayout.EAST);

		JPanel panel_2 = new JPanel();
		panel_2.setPreferredSize(new Dimension(435, 25));
		selectFilePnl.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JLabel lblDestination = new JLabel("Destination:");
		panel_2.add(lblDestination, BorderLayout.WEST);

		JLabel lblDest = new JLabel("(Ex. D:\\THISPROJECT\\Sources\\Subs\\)");
		panel_2.add(lblDest);
		lblDest.setEnabled(false);

		JButton destinationButt = new JButton("Browse");
		destinationButt.setHorizontalTextPosition(SwingConstants.LEFT);
		panel_2.add(destinationButt, BorderLayout.EAST);
		
		JPanel Options = new JPanel();
		Options.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		selectFilePnl.add(Options);
		
				JPanel optionsPnl = new JPanel();
				Options.add(optionsPnl);
				
						maxCharTf = new JTextField();
						maxCharTf.addFocusListener(new FocusAdapter() {
							@SuppressWarnings("static-access")
							@Override
							public void focusLost(FocusEvent arg0) {
								Pattern numbersOnly = Pattern.compile("\\D");
								Matcher matcher = numbersOnly.matcher(maxCharTf.getText());
								if (matcher.find()){
									maxCharTf.setText("");
									new JOptionPane().showMessageDialog(null, "numbers only, reseted to 40");
									maxCharTf.setText("40");
								}
							}
						});
						maxCharTf.setText("40");
						optionsPnl.add(maxCharTf);
						maxCharTf.setColumns(3);
						
								JLabel lblNewLabel = new JLabel("Max sub length");
								
										optionsPnl.add(lblNewLabel);
										
										JCheckBox previewCb = new JCheckBox("Preview");
										Options.add(previewCb);

		JButton createSrtButt = new JButton("Create SRT");
		destinationButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseDest();
			}

			private void chooseDest() {
				int result = outputJfc.showOpenDialog(getParent());

				if (result == JFileChooser.APPROVE_OPTION) {
					Txt2Srt.setDestination(outputJfc.getSelectedFile());
					lblDest.setText(outputJfc.getSelectedFile().getAbsolutePath());
					lblDest.setEnabled(true);
				}

			}
		});
		selectInputButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseInput();
			}

			private void chooseInput() {
				int result = inputJfc.showOpenDialog(getParent());

				if (result == JFileChooser.APPROVE_OPTION) {
					Txt2Srt.setFile2Convert(inputJfc.getSelectedFile());
					lblSelectedFileUrl.setText(inputJfc.getSelectedFile().getAbsolutePath());
					lblSelectedFileUrl.setEnabled(true);
					createSrtButt.setEnabled(true);
					lblDest.setText("(Same as Source)-->" + Txt2Srt.getFile2Convert().getParentFile().getAbsolutePath()
							+ "\\" + Txt2Srt.getFile2Convert().getName().replaceFirst(".txt", "") + ".srt");
					Txt2Srt.setDestination(new File(Txt2Srt.getFile2Convert().getParentFile().getAbsolutePath()));

				}

			}
		});

		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		add(panel, BorderLayout.SOUTH);

		createSrtButt.setEnabled(false);
		createSrtButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Txt2Srt.setCharMax((Integer.valueOf(maxCharTf.getText())));

				Thread exec = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Txt2Srt.doIt(previewCb.isSelected());
							if (previewCb.isSelected()){
								Txt2Srt.openPreviewPnl();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				exec.start();
			}
		});
		panel.add(createSrtButt);
	}
}
