package com.demo.service;

import java.util.List;


import com.demo.model.Match;

public interface MatchService {
	//get all matches
	List<Match> getAllMatches();
	
	//get all matches
	List<Match> getLiveMatches();
	
	//get cwc2023 point table 
	List<List<String>> getPointTable();
} 
