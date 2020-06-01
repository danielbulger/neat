package com.danielbulger.neat;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private Phenotype best;

	private final List<Phenotype> generation = new ArrayList<>();

	private final List<Species> species = new ArrayList<>();

}
