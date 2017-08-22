package main.java.ta4j_strategies;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import main.java.ta4j_strategies.strategies.SimpleRangeScalper;

/**
 * Example how you could use and test strategies with example data of this repository
 */
public class Run {


    public static void main(String[] args) {

        // load data as TimeSeries
        Loader loader = new Loader();
        TimeSeries series = loader.getMinuteTimeSeries("ta4j-strategies\\src\\main\\data\\fb_minutes.csv", "Facebook");

        // create and initialize a strategy
        SimpleRangeScalper simpleRangeScalper = new SimpleRangeScalper();
        simpleRangeScalper.initStrategy(series);

        // run strategy on time series and analyse results
        StrategyAnalyser analyser = new StrategyAnalyser();
        analyser.printAllResults(simpleRangeScalper);

        // change parameters of the strategy and run again
        simpleRangeScalper.setParams(20, Decimal.valueOf(0.5));
        analyser.printAllResults(simpleRangeScalper);
    }
}
