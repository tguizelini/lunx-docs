package tech.buildrun.sboot_dynamodb.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import tech.buildrun.sboot_dynamodb.controller.dto.ScoreDto;
import tech.buildrun.sboot_dynamodb.entity.PlayerHistoryEntity;

@RestController
@RequestMapping("/v1/players")
public class PlayerController {

    @Autowired
    private DynamoDbTemplate db;

    @PostMapping("/{username}/games")
    public ResponseEntity<Void> save(
        @PathVariable("username") String username,
        @RequestBody ScoreDto scoreDto
    ) {
        var entity = new PlayerHistoryEntity();
        entity.setUsername(username);
        entity.setGameId(UUID.randomUUID());
        entity.setScore(scoreDto.score());
        entity.setCreatedAt(Instant.now());

        db.save(entity);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/games")
    public ResponseEntity<List<PlayerHistoryEntity>> list(
        @PathVariable("username") String username
    ) {
        var key = Key.builder().partitionValue(username).build();
        var condition = QueryConditional.keyEqualTo(key);

        var qs = QueryEnhancedRequest.builder()
                .queryConditional(condition)
                .build();

        var history = db.query(qs, PlayerHistoryEntity.class);

        return ResponseEntity.ok(history.items().stream().toList());
    }

    @GetMapping("/{username}/games/{gameId}")
    public ResponseEntity<PlayerHistoryEntity> find(
            @PathVariable("username") String username,
            @PathVariable("gameId") String gameId
    ) {
        var key = Key.builder()
            .partitionValue(username)
            .sortValue(gameId)
            .build();

        var entity = db.load(key, PlayerHistoryEntity.class);

        return entity != null ?
            ResponseEntity.ok(entity)
            : ResponseEntity.notFound().build();
    }

    @PutMapping("/{username}/games/{gameId}")
    public ResponseEntity<PlayerHistoryEntity> update(
            @PathVariable("username") String username,
            @PathVariable("gameId") String gameId,
            @RequestBody ScoreDto scoreDto
    ) {
        var key = Key.builder()
                .partitionValue(username)
                .sortValue(gameId)
                .build();

        var entity = db.load(key, PlayerHistoryEntity.class);

        if (entity == null) return ResponseEntity.notFound().build();

        entity.setScore(scoreDto.score());
        db.update(entity);

        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{username}/games/{gameId}")
    public ResponseEntity<PlayerHistoryEntity> delete(
            @PathVariable("username") String username,
            @PathVariable("gameId") String gameId
    ) {
        var key = Key.builder()
                .partitionValue(username)
                .sortValue(gameId)
                .build();

        var entity = db.load(key, PlayerHistoryEntity.class);

        if (entity == null) return ResponseEntity.notFound().build();

        db.delete(entity);

        return ResponseEntity.noContent().build();
    }
}
