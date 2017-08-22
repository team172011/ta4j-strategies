package main.java.ta4j_strategies;

import com.opencsv.CSVReader;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for loading example/testing data from this repository
 */
public class Loader{

    private static final DateTimeFormatter DATE_FORMAT_HOURLY_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s z");
    private static final DateTimeFormatter DATE_FORMAT_Daily = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    public TimeSeries getHourlyTimeSeries(String pathToCsv, String name){

        List<Tick> ticks = new ArrayList<>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(pathToCsv));
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                ZonedDateTime date = ZonedDateTime.parse(line[0]+" "+line[1]+" PST", DATE_FORMAT_HOURLY_MINUTE);
                double open = Double.parseDouble(line[2]);
                double high = Double.parseDouble(line[3]);
                double low = Double.parseDouble(line[4]);
                double close = Double.parseDouble(line[5]);
                double volume = Double.parseDouble(line[6]);

                ticks.add(new Tick(date, open, high, low, close, volume));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new TimeSeries(name, ticks);
    }

    public TimeSeries getMinuteTimeSeries(String pathToCsv, String name){
        return getHourlyTimeSeries(pathToCsv, name);
    }

    public TimeSeries getDailyTimeSerie(String pathToCsv, String name){

        List<Tick> ticks = new ArrayList<>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(pathToCsv));
            String[] line;
            reader.readNext();
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                ZonedDateTime date = LocalDate.parse(line[0], DATE_FORMAT_Daily).atStartOfDay(ZoneId.systemDefault());
                double close = Double.parseDouble(line[1]);
                double volume = Double.parseDouble(line[2]);
                double open = Double.parseDouble(line[3]);
                double high = Double.parseDouble(line[4]);
                double low = Double.parseDouble(line[5]);

                ticks.add(new Tick(date, open, high, low, close, volume));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TimeSeries(name, ticks);
    }

	
	
}