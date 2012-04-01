package com.zw.ws;

import com.zw.markov.MarkovState;

public interface ReCompositor {
	MarkovState recomposite(MarkovState state);
	double getPosibility();
	double getTimeCost();
	double getPriceCost();
}
