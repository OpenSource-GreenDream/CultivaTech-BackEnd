package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.community.domain.repositories.CommentRepository;
import greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities.CommentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryAdapter implements CommentRepository {
    private final CommentJpaRepository jpa;
    public CommentRepositoryAdapter(CommentJpaRepository jpa) { this.jpa = jpa; }
    @Override public List<Comment> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Optional<Comment> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Comment> findByTargetProfileId(Long id) { return jpa.findAllByTargetProfileId(id).stream().map(this::toDomain).toList(); }
    @Override public List<Comment> findByFieldId(Long id) { return jpa.findAllByFieldId(id).stream().map(this::toDomain).toList(); }
    @Override public List<Comment> findByProductId(Long id) { return jpa.findAllByProductId(id).stream().map(this::toDomain).toList(); }
    @Override public Comment save(Comment c) { return toDomain(jpa.save(toEntity(c))); }
    @Override public Comment update(Comment c) {
        CommentEntity e = jpa.findById(c.getId()).orElseThrow(); e.setContent(c.getContent()); e.setRating(c.getRating());
        return toDomain(jpa.save(e));
    }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public void deleteAll() { jpa.deleteAll(); }
    private CommentEntity toEntity(Comment c) {
        CommentEntity e = new CommentEntity(); e.setAuthorProfileId(c.getAuthorProfileId()); e.setTargetType(c.getTargetType());
        e.setTargetProfileId(c.getTargetProfileId()); e.setFieldId(c.getFieldId()); e.setProductId(c.getProductId());
        e.setContent(c.getContent()); e.setRating(c.getRating()); return e;
    }
    private Comment toDomain(CommentEntity e) {
        return Comment.restore(e.getId(), e.getAuthorProfileId(), e.getTargetType(), e.getTargetProfileId(), e.getFieldId(), e.getProductId(), e.getContent(), e.getRating());
    }
}
