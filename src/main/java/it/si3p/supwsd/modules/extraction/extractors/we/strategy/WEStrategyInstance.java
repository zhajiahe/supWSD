package it.si3p.supwsd.modules.extraction.extractors.we.strategy;

/**
 * @author papandrea
 *
 */
public class WEStrategyInstance {

	private static WEStrategyInstance instance;

	private WEStrategyInstance() {

	}

	public static WEStrategyInstance getInstance() {

		if (instance == null)
			instance = new WEStrategyInstance();

		return instance;
	}

	public WEIntegrationStrategy getIntegrationStrategy(WEStrategy strategy,int window)  {

		WEIntegrationStrategy integrationStrategy = null;

		switch (strategy) {

		case EXP:

			integrationStrategy = new WEExponentialDecayStrategy(window);
			break;

		case FRA:

			integrationStrategy = new WEFractionalDecayStrategy(window);
			break;
			
		case AVG:

			integrationStrategy = new WEAverageStrategy(window);
			break;
		}
		
		return integrationStrategy;
	}
}
