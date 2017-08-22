package main.java.ta4j_strategies;


import eu.verdelhan.ta4j.Order;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.TradingRecord;

import java.util.List;

public interface StrategyBuilder{

    /**
     * initialize the strategy with default parameters
     * @param series the time serie to initilize the strategy with
     */
    void initStrategy(TimeSeries series);

    /**
     * Create and get a long or short strategy
     * @param type the kind of order that should be used in the strategy
     * @return a strategy (long, short) corresponding to the order type, null if order type is not supported
     */
    Strategy buildStrategy(Order.OrderType type);

    /**
     * Runs the strategy on the time series it is initialized with
     * @param type the kind of order that should be used in the strategy
     * @return results as a trading record
     */
    TradingRecord getTradingRecord(Order.OrderType type);

    /**
     * Get the current time series the strategy is initilized with
     * @return current time series the strategy is initilized with
     */
    TimeSeries getTimeSeries();

    /**
     * Get the name of the strategy
     * @return name of the strategy
     */
    String getName();

    /**
     * Get the parameters of the strategy
     * @return a list of parameters of the strategy
     * TODO: find better solution to handle the strategie parameter
     */
    List<String> getParamters();

}
