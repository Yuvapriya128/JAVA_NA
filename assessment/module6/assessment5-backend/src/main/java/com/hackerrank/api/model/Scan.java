package com.hackerrank.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Scan implements Serializable {

    private boolean deleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String domainName;
    private Integer numPages;
    private Integer numBrokenLinks;
    private Integer numMissingImages;
    

    // Private constructor
    private Scan(Builder builder) {
        this.deleted = builder.deleted;
        this.id = builder.id;
        this.domainName = builder.domainName;
        this.numPages = builder.numPages;
        this.numBrokenLinks = builder.numBrokenLinks;
        this.numMissingImages = builder.numMissingImages;
    }

//    public Scan() {
//		// TODO Auto-generated constructor stub
//	}

	// Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private boolean deleted = false;
        private Long id;
        private String domainName;
        private Integer numPages;
        private Integer numBrokenLinks;
        private Integer numMissingImages;

        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder domainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public Builder numPages(Integer numPages) {
            this.numPages = numPages;
            return this;
        }

        public Builder numBrokenLinks(Integer numBrokenLinks) {
            this.numBrokenLinks = numBrokenLinks;
            return this;
        }

        public Builder numMissingImages(Integer numMissingImages) {
            this.numMissingImages = numMissingImages;
            return this;
        }

        public Scan build() {
            return new Scan(this);
        }
    }

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getNumPages() {
		return numPages;
	}

	public void setNumPages(Integer numPages) {
		this.numPages = numPages;
	}

	public Integer getNumBrokenLinks() {
		return numBrokenLinks;
	}

	public void setNumBrokenLinks(Integer numBrokenLinks) {
		this.numBrokenLinks = numBrokenLinks;
	}

	public Integer getNumMissingImages() {
		return numMissingImages;
	}

	public void setNumMissingImages(Integer numMissingImages) {
		this.numMissingImages = numMissingImages;
	}
    
}