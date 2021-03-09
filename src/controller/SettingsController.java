package controller;

import view.SettingsView;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import controller.Calculations.Units;

public class SettingsController {
    private static final String CONFIG_FILE = "settings.cfg"; //file settings are stored in
    private SettingsView settingsWindow;

    public SettingsController(InputController inputController){
        Scanner input = null;
        try{
           input = new Scanner(new File(CONFIG_FILE));
        } catch (FileNotFoundException e) {
        }
        settingsWindow = new SettingsView(input);

        //do this the first time to populate all the values in the input window from the saved settings
        try {
            //we have to do this first so we can
            inputController.getInputWindow().getGaugeLengthInputField().setText(String.valueOf(1.0));

            if (settingsWindow.getDefaultUnits().equals("English")){
                inputController.getInputWindow().setCurrentUnitSystem(Units.ENGLISH);
            }else{
                inputController.getInputWindow().setCurrentUnitSystem(Units.METRIC);
            }
            inputController.getInputWindow().getUnitSelectionBox().setSelectedItem(settingsWindow.getDefaultUnits());

            //set the input gauge length to be the default gauge length
            double value = settingsWindow.getDefaultGaugeLength();
            inputController.getInputWindow().getGaugeLengthInputField().setText(String.valueOf(value));
        }
        catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(settingsWindow,"Default Gauge Length is not a properly formatted number."," Bad Gauge Length",JOptionPane.ERROR_MESSAGE);
        }

        settingsWindow.getSaveButton().addActionListener(e -> settingsWindow.setVisible(false));
        if(input != null){
            input.close();
        }

        //stores settings in a file and updates the values in the input window
        settingsWindow.getSaveButton().addActionListener(e -> {
            try {
                double value = settingsWindow.getDefaultGaugeLength();
                inputController.getInputWindow().getGaugeLengthInputField().setText(String.valueOf(value)); //settings gauge length to input gauge length
                if (settingsWindow.getDefaultUnits().equals("English")){
                    inputController.getInputWindow().setCurrentUnitSystem(Units.ENGLISH);
                }else{
                    inputController.getInputWindow().setCurrentUnitSystem(Units.METRIC);
                }
                inputController.getInputWindow().getUnitSelectionBox().setSelectedItem(settingsWindow.getDefaultUnits());

                PrintWriter out = new PrintWriter(new FileOutputStream(CONFIG_FILE));
                out.println(settingsWindow.getDefaultUnitSelectionBox().getSelectedItem());
                out.println(value);
                out.close();
            } catch (FileNotFoundException exception) {
            }
            catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(settingsWindow,"Default Gauge Length is not a properly formatted number."," Bad Gauge Length",JOptionPane.ERROR_MESSAGE);
            }
        });

        settingsWindow.getDefaultUnitSelectionBox().addActionListener(e -> {
            //convert the values to the correct unit system
            double convertedValue;
            if (settingsWindow.getDefaultUnitSelectionBox().getSelectedItem().equals("English")) {
                convertedValue = Calculations.convertLength(settingsWindow.getCurrentUnitSystem(), Units.ENGLISH, settingsWindow.getDefaultGaugeLength());
                settingsWindow.setCurrentUnitSystem(Units.ENGLISH);
            }else{
                convertedValue = Calculations.convertLength(settingsWindow.getCurrentUnitSystem(), Units.METRIC, settingsWindow.getDefaultGaugeLength());
                settingsWindow.setCurrentUnitSystem(Units.METRIC);
            }

            settingsWindow.getDefaultGaugeLengthField().setText(String.format("%.10f", convertedValue));
        });
    }

    public SettingsView getSettingsWindow(){
        return settingsWindow;
    }

}
