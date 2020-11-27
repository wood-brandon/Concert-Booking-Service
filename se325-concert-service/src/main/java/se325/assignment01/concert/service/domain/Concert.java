package se325.assignment01.concert.service.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.common.jackson.LocalDateTimeDeserializer;
import se325.assignment01.concert.common.jackson.LocalDateTimeSerializer;

/**
 * Class to represent a concert. Each concert class has:
 * id           unique identifier
 * title        title of the concert
 * imagename    corresponding image for the concert via concerts/imagename
 * blurb        short description of concert
 * dates        the concert's scheduled dates and times (represented as a Set of LocalDateTime instances).
 * performers   the performers in the concert

 */

@Entity
@Table(name = "CONCERTS")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @Column(name = "BLURB", columnDefinition="LONGTEXT")
    private String blurb;

    @ElementCollection
    @CollectionTable(name = "CONCERT_DATES", joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @Column(name = "DATE", columnDefinition = "TIMESTAMP")
    private Set<LocalDateTime> dates;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "CONCERT_PERFORMER",
            joinColumns = @JoinColumn(name = "CONCERT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    private List<Performer> performers = new ArrayList<>();

    public Concert() {
    }

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public Set<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(Set<LocalDateTime> dates) {
        this.dates = dates;
    }

    public List<Performer> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Performer> performers) {
        this.performers = performers;
    }


}
