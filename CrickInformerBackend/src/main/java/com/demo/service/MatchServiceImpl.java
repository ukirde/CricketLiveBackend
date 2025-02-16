package com.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.persistence.Query;
import java.util.Optional;

import com.demo.dao.MatchDao;
import com.demo.model.Match;

@Service
public class MatchServiceImpl implements MatchService{

	private MatchDao matchDao;
	public MatchServiceImpl(@Lazy MatchDao matchDao) {
		this.matchDao = matchDao;
	}

	@Override
	public List<Match> getAllMatches() {
		return this.matchDao.findAll();
	}

	@Override
	public List<Match> getLiveMatches() {
		 List<Match> matches = new ArrayList<>();
	        try {
	            String url = "https://www.cricbuzz.com/cricket-match/live-scores";
	            Document document = Jsoup.connect(url).get();
	            Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-tms-itm");
	            for (Element match : liveScoreElements) {
	                HashMap<String, String> liveMatchInfo = new LinkedHashMap<>();
	                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr").select("a").text();
	                String matchNumberVenue = match.select("span").text();
	                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
	                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
	                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
	                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
	                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
	                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
	                String textLive = match.select("div.cb-text-live").text();
	                String textComplete = match.select("div.cb-text-complete").text();
	                //getting match link
	                String matchLink = match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href").toString();

	                
	 
	                
	                Match match1 = new Match();
	                match1.setTeamHeading(teamsHeading);
	                match1.setMatchNumberVenue(matchNumberVenue);
	                match1.setBattingTeam(battingTeam);
	                match1.setBattingTeamScore(score);
	                match1.setBowlTeam(bowlTeam);
	                match1.setBowlTeamScore(bowlTeamScore);
	                match1.setLiveText(textLive);
	                match1.setMatchlink(matchLink);
	                match1.setTextComplete(textComplete);
	                match1.setMatchStatus();


	                matches.add(match1);

            //update the match in database
	            updateMatchInDb(match1);    

	                


	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return matches;
	}

	private void updateMatchInDb(Match match1) {
	    List<Match> matches = this.matchDao.findByTeamHeading(match1.getTeamHeading());

	    if (!matches.isEmpty()) { 
	        Match existingMatch = matches.get(0); // ✅ फक्त जर यादी रिकामी नसेल तर get(0) वापरा
	        match1.setMatchId(existingMatch.getMatchId());
	    }

	    matchDao.save(match1);
	}


	@Override
	public List<List<String>>getPointTable() {
		 List<List<String>> pointTable = new ArrayList<>();
	        String tableURL = "https://www.cricbuzz.com/cricket-stats/points-table/test/icc-world-test-championship";

	        try {
	            // Add User-Agent to simulate a browser request
	            Document document = Jsoup.connect(tableURL)
	                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
	                    .timeout(10 * 1000)
	                    .get();

	            Elements table = document.select("table.cb-srs-pnts");

	            // Extract headers
	            Elements tableHeads = table.select("thead>tr>*");
	            List<String> headers = new ArrayList<>();
	            tableHeads.forEach(element -> headers.add(element.text()));
	            pointTable.add(headers);

	            // Extract tbody rows
	            Elements bodyTrs = table.select("tbody>tr");
	            for (var tr : bodyTrs) {
	                List<String> row = new ArrayList<>();
	                Elements tds = tr.select("td");
	                tds.forEach(td -> row.add(td.text()));
	                pointTable.add(row);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return pointTable;
	    }

	    public static void main(String[] args) {
	        List<List<String>> table = getPointTable();
	        System.out.println(table);
	    }
}
