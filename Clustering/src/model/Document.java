/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;


/**
 *
 * @author puspaningtyas
 */
public class Document implements Comparable<Document> {

    private int id;
    private String content; 
    private String realContent; 
    private ArrayList<Posting> listOfClusteringPosting = new ArrayList<Posting>();
    private String title;

    public Document() {
    }

    public Document(int id) {
        this.id = id;
    }

    public Document(String content) {
        this.content = content;
        this.realContent = content;
    }

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
        this.realContent = content;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
    
     public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public String[] getListofTerm() {
        String value = this.getContent();
        value = value.replaceAll("[.,?!]", "");
        return value.split(" ");
    }

    public ArrayList<Posting> getListofPosting() {
        // panggil fungsi getListOfTerm
        String tempString[] = getListofTerm();
        ArrayList<Posting> result = new ArrayList<Posting>();
        for (int i = 0; i < tempString.length; i++) {
            if (i == 0) {
                Posting temPosting = new Posting(tempString[0], this);
                result.add(temPosting);
            } else {
                Collections.sort(result);
                Posting temPosting = new Posting(tempString[i], this);
                int indexCari = Collections.binarySearch(result, temPosting);
                if (indexCari < 0) {
                    result.add(temPosting);
                } else {
                    int tempNumber = result.get(indexCari).getNumberOfTerm() + 1;
                    result.get(indexCari).setNumberOfTerm(tempNumber);
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(Document doc) {
        return id - doc.getId();
    }

    public void readFile(int idDoc, File file) {
        this.id = idDoc;
        String fileName = file.getName();
        this.title = fileName.substring(0, fileName.lastIndexOf("."));
        try {
            FileReader bacaFile = new FileReader(file);
            try ( 
                    BufferedReader bufReader = new BufferedReader(bacaFile)) {
                String str;
                while ((str = bufReader.readLine()) != null) {
                    this.setRealContent(str);
                    this.setContent(str);
                }
            }
        }
        catch (FileNotFoundException f) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", content=" + content + ", realContent=" + realContent + '}';
    }

    public void removeStopWords() {
        String text = content;
        Version matchVersion = Version.LUCENE_7_7_0; // Substitute desired Lucene version for XY
        Analyzer analyzer = new StandardAnalyzer();
        analyzer.setVersion(matchVersion);
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        TokenStream tokenStream = analyzer.tokenStream(
                "myField",
                new StringReader(text.trim()));
        tokenStream = new StopFilter(tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                sb.append(term + " ");
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
        content = sb.toString();
    }

    public void stemming() {
        String text = content;
        Version matchVersion = Version.LUCENE_7_7_0; // Substitute desired Lucene version for XY
        Analyzer analyzer = new StandardAnalyzer();
        analyzer.setVersion(matchVersion);
        TokenStream tokenStream = analyzer.tokenStream(
                "myField",
                new StringReader(text.trim()));
        tokenStream = new PorterStemFilter(tokenStream);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                sb.append(term + " ");
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
        content = sb.toString();
    }

    /**
     * @return the realContent
     */
    public String getRealContent() {
        return realContent;
    }

    /**
     * @param realContent the realContent to set
     */
    public void setRealContent(String realContent) {
        this.realContent = realContent;
    }

    public void IndonesiaStemming() {
        Version matchVersion = Version.LUCENE_7_7_0; 
        Analyzer analyzer = new IndonesianAnalyzer();
        analyzer.setVersion(matchVersion);
        CharArraySet stopWords = IndonesianAnalyzer.getDefaultStopSet();
        TokenStream tokenStream = analyzer.tokenStream(
                "myField",
                new StringReader(realContent.trim()));
        tokenStream = new StopFilter(tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                sb.append(term + " ");
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
        content = sb.toString();
    }

    public ArrayList<Posting> getListOfClusteringPosting() {
        return listOfClusteringPosting;
    }

    /**
     * @param listOfClusteringPosting the listOfClusteringPosting to set
     */
    public void setListOfClusteringPosting(ArrayList<Posting> listOfClusteringPosting) {
        this.listOfClusteringPosting = listOfClusteringPosting;
    }

    

}
