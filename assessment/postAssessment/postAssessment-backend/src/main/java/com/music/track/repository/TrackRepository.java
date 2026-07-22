package com.music.track.repository;

import com.music.track.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    Track findByTitleIgnoreCase(String title);
}
