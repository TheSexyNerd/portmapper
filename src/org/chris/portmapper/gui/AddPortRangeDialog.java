package org.chris.portmapper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.Protocol;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

/**
 * This class represents the dialog that can add a range of ports to the edit
 * presets dialog.
 * 
 * @author chris
 * @version $Id$
 */
public class AddPortRangeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static String DIALOG_NAME = "add_port_range_dialog";

	private final static String ACTION_ADD = DIALOG_NAME + ".add";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";

	private final EditPresetDialog editPresetDialog;

	private JTextField internalPortFrom;
	private JTextField internalPortTo;

	private JTextField externalPortFrom;
	private JTextField externalPortTo;

	private JCheckBox internalEqualsExternalPorts;

	private JButton okButton;

	private JComboBox protocolComboBox;

	/**
	 * 
	 * @param portMappingPreset
	 */
	public AddPortRangeDialog(EditPresetDialog editPresetDialog) {
		super(PortMapperApp.getInstance().getMainFrame(), true);

		this.editPresetDialog = editPresetDialog;

		logger.debug("Create settings dialog");
		this.setContentPane(this.getDialogPane());

		this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setName(DIALOG_NAME);
		this.setModal(true);
		this.pack();

		// Register an action listener that closes the window when the ESC
		// button is pressed
		KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
				true);
		ActionListener windowCloseActionListener = new ActionListener() {
			public final void actionPerformed(final ActionEvent e) {
				cancel();
			}
		};
		getRootPane().registerKeyboardAction(windowCloseActionListener,
				escKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private static JLabel createLabel(String postfix) {
		String completeName = DIALOG_NAME + "." + postfix;
		JLabel newLabel = new JLabel(completeName);
		newLabel.setName(completeName);
		return newLabel;
	}

	private JPanel getDialogPane() {
		final ActionMap actionMap = PortMapperApp.getInstance().getContext()
				.getActionMap(this.getClass(), this);

		JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left]", // Column Constraints
				"")); // Row Constraints

		internalPortFrom = new JTextField(5);
		internalPortTo = new JTextField(5);
		externalPortFrom = new JTextField(5);
		externalPortTo = new JTextField(5);

		externalPortFrom.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (internalEqualsExternalPorts.isSelected()) {
					internalPortFrom.setText(externalPortFrom.getText());
				}
			}

			public void keyTyped(KeyEvent e) {
				// ignored
			}

			public void keyPressed(KeyEvent e) {
				// ignored
			}
		});

		externalPortTo.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (internalEqualsExternalPorts.isSelected()) {
					internalPortTo.setText(externalPortTo.getText());
				}
			}

			public void keyTyped(KeyEvent e) {
				// ignored
			}

			public void keyPressed(KeyEvent e) {
				// ignored
			}
		});

		protocolComboBox = new JComboBox();
		protocolComboBox.addItem(Protocol.TCP);
		protocolComboBox.addItem(Protocol.UDP);
		protocolComboBox.setSelectedIndex(0);

		internalEqualsExternalPorts = new JCheckBox(
				"add_port_range_dialog.external_equal_internal");
		internalEqualsExternalPorts
				.setName("add_port_range_dialog.external_equal_internal");
		internalEqualsExternalPorts.setSelected(true);
		internalEqualsExternalPorts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("Checkbox value changed");
				internalPortFrom.setEnabled(!internalEqualsExternalPorts
						.isSelected());
				internalPortTo.setEnabled(!internalEqualsExternalPorts
						.isSelected());

				if (internalEqualsExternalPorts.isSelected()) {
					internalPortTo.setText(externalPortTo.getText());
					internalPortFrom.setText(externalPortFrom.getText());
				}
			}
		});

		internalPortFrom.setEnabled(!internalEqualsExternalPorts.isSelected());
		internalPortTo.setEnabled(!internalEqualsExternalPorts.isSelected());

		dialogPane.add(createLabel("protocol"));
		dialogPane.add(protocolComboBox, "wrap");

		dialogPane.add(createLabel("external_ports_from"));
		dialogPane.add(externalPortFrom);
		dialogPane.add(createLabel("external_ports_to"));
		dialogPane.add(externalPortTo, "wrap");

		dialogPane.add(internalEqualsExternalPorts, "left, span 4, wrap");

		dialogPane.add(createLabel("internal_ports_from"));
		dialogPane.add(internalPortFrom);
		dialogPane.add(createLabel("internal_ports_to"));
		dialogPane.add(internalPortTo, "wrap");

		dialogPane.add(new JButton(actionMap.get(ACTION_CANCEL)),
				"tag cancel, span 2");
		okButton = new JButton(actionMap.get(ACTION_ADD));
		dialogPane.add(okButton, "tag ok, span 2, wrap");

		return dialogPane;
	}

	/**
	 * This method is executed when the user clicks the save button. The method
	 * saves the entered preset
	 */
	@Action(name = ACTION_ADD)
	public void addPortRange() {
		final int internalPortFrom;
		final int internalPortTo;
		final int externalPortFrom;
		final int externalPortTo;

		try {
			internalPortFrom = Integer
					.parseInt(this.internalPortFrom.getText());
			internalPortTo = Integer.parseInt(this.internalPortTo.getText());
			externalPortFrom = Integer
					.parseInt(this.externalPortFrom.getText());
			externalPortTo = Integer.parseInt(this.externalPortTo.getText());
		} catch (NumberFormatException e) {
			showErrorMessage("add_port_range_dialog.invalid_number.title",
					"add_port_range_dialog.invalid_number.message");
			return;
		}

		if (internalPortFrom >= internalPortTo) {
			showErrorMessage(
					"add_port_range_dialog.invalid_internal_range.title",
					"add_port_range_dialog.invalid_internal_range.message");
			return;
		}

		if (externalPortFrom >= externalPortTo) {
			showErrorMessage(
					"add_port_range_dialog.invalid_external_range.title",
					"add_port_range_dialog.invalid_external_range.message");
			return;
		}

		if (internalPortTo - internalPortFrom != externalPortTo
				- externalPortFrom) {
			showErrorMessage(
					"add_port_range_dialog.invalid_range_length.title",
					"add_port_range_dialog.invalid_range_length.message");
			return;
		}

		for (int i = internalPortFrom; i <= internalPortTo; i++) {
			final int internalPort = i;
			final int externalPort = (internalPort - internalPortFrom)
					+ externalPortFrom;
			final Protocol selectedProtocol = (Protocol) protocolComboBox
					.getSelectedItem();
			editPresetDialog.addPort(selectedProtocol, internalPort,
					externalPort);
		}

		this.dispose();
	}

	private void showErrorMessage(String titleKey, String messageKey) {
		final ResourceMap resourceMap = PortMapperApp.getResourceMap();
		JOptionPane.showMessageDialog(this, resourceMap.getString(messageKey),
				resourceMap.getString(titleKey), JOptionPane.ERROR_MESSAGE);
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.dispose();
	}
}