package com.myapp.demo.urlshortener.dao;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * DAO object to manage URL shortener
 */
@Entity
@Table(indexes = {
    @Index(name = "urlindex", columnList = "url", unique = true)
  })
@Access(value=AccessType.FIELD)
public class URLShortener {
    @Id
    private String id;
    
	@Column(name = "url")
    private String url;

    //TODO : Batch/schedule to clean old/expire data
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updateDate;

    public URLShortener() {
    }

    public URLShortener(String id, String url) {
        this.id = id;
        this.url = url;
        this.updateDate = new Date();
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public String toString() {
        return "URLShortener{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}
