package main.java.ta4j_strategies.strategies;

import eu.verdelhan.ta4j.*;
import eu.verdelhan.ta4j.indicators.simple.*;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.trackers.bollinger.BollingerBandsUpperIndicator;
import eu.verdelhan.ta4j.trading.rules.*;
import main.java.ta4j_strategies.StrategyBuilder;

import java.util.ArrayList;
import java.util.List;


/*
    http://www.investopedia.com/terms/s/scalping.asp
    http://forexop.com/strategy/simple-range-scalper/
 */
public class SimpleRangeScalper implements StrategyBuilder {

    private TimeSeries series;

    private ClosePriceIndicator closePrice;
    private Indicator<Decimal> maxPrice;
    private Indicator<Decimal> minPrice;

    private BollingerBandsUpperIndicator upperBollingerBand;
    private BollingerBandsMiddleIndicator middleBollingerBand;
    private BollingerBandsLowerIndicator lowerBollingeBand;

    // parameters
    private Decimal takeProfitValue;
    private int emaForBollingerBandValue;
    /**
     * Constructor
     */
    public SimpleRangeScalper(){}

    public SimpleRangeScalper(TimeSeries series){
        initStrategy(series);
    }

    @Override
    public void initStrategy(TimeSeries series) {
        this.series = series;
        this.minPrice = new MinPriceIndicator(this.series);
        this.closePrice = new ClosePriceIndicator(this.series);
        this.maxPrice = new MaxPriceIndicator(this.series);
        setParams(14, Decimal.valueOf(0.01));
    }

    @Override
    public Strategy buildStrategy(Order.OrderType type){
        if (type.equals(Order.OrderType.SELL))
            return getShortStrategy();
        return getLongStrategy();
    }

    @Override
    public TradingRecord getTradingRecord(Order.OrderType type) {
        return this.series.run(buildStrategy(type), type);
    }

    @Override
    public TimeSeries getTimeSeries(){
        return this.series;
    }

    @Override
    public String getName(){
        return "Simple Range Scalper";
    }

    @Override
    public List<String> getParamters(){
        ArrayList<String> parameters = new ArrayList<String>();
        String takeProfit = "Take Profit: "+ this.takeProfitValue;
        String ema = "EMA :"+ this.emaForBollingerBandValue;
        parameters.add(takeProfit);
        parameters.add(ema);
        return  parameters;
    }

    /**
     * call this function to change the parameter of the strategy
     * @param emaForBollingerBandValue exponential moving average the bollinger bands are based on
     * @param takeProfitValue close a trade if this percentage profit is reached
     */
    public void setParams(int emaForBollingerBandValue, Decimal takeProfitValue){
        this.takeProfitValue = takeProfitValue;
        this.emaForBollingerBandValue = emaForBollingerBandValue;

        EMAIndicator ema = new EMAIndicator(this.closePrice, emaForBollingerBandValue);
        StandardDeviationIndicator standardDeviation = new StandardDeviationIndicator(this.closePrice, emaForBollingerBandValue);
        this.middleBollingerBand = new BollingerBandsMiddleIndicator(ema);
        this.lowerBollingeBand = new BollingerBandsLowerIndicator(this.middleBollingerBand, standardDeviation);
        this.upperBollingerBand = new BollingerBandsUpperIndicator(this.middleBollingerBand, standardDeviation);
    }

    private Strategy getLongStrategy() {

        Indicator<Decimal> d_upper_middle = new DifferenceIndicator(this.upperBollingerBand, this.middleBollingerBand);
        // exit if half way up to middle reached
        Indicator<Decimal> threshold = new MultiplierIndicator(d_upper_middle, Decimal.valueOf(0.5));

        Rule entrySignal = new CrossedUpIndicatorRule(this.maxPrice, this.upperBollingerBand);
        Rule entrySignal2 = new UnderIndicatorRule(this.minPrice, this.upperBollingerBand);

        Rule exitSignal = new CrossedDownIndicatorRule(this.closePrice, threshold);
        Rule exitSignal2 = new StopGainRule(closePrice, this.takeProfitValue);

        return new Strategy(entrySignal.and(entrySignal2), exitSignal.or(exitSignal2));
    }

    private Strategy getShortStrategy(){

        Indicator<Decimal> d_middle_lower = new DifferenceIndicator(this.middleBollingerBand, this.lowerBollingeBand);
        // exit if half way down to middle reached
        Indicator<Decimal> threshold = new MultiplierIndicator(d_middle_lower, Decimal.valueOf(0.5));

        Rule entrySignal = new CrossedDownIndicatorRule(this.minPrice, this.lowerBollingeBand);
        Rule entrySignal2 = new OverIndicatorRule(this.maxPrice, this.lowerBollingeBand);

        Rule exitSignal = new CrossedUpIndicatorRule(this.closePrice, threshold);
        Rule exitSignal2 = new StopLossRule(closePrice, this.takeProfitValue); // stop loss long = stop gain short?

        return new Strategy(entrySignal.and(entrySignal2), exitSignal.or(exitSignal2));
    }
}
