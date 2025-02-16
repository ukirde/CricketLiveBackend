package com.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.demo.model.Match;

public interface MatchDao extends JpaRepository<Match, Integer>{
	//match matching 
    List<Match> findByTeamHeading(String teamHeading);
	
	

}
