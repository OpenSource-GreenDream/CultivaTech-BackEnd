package greendream.cultivatech.platform.community.application.services;

import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import greendream.cultivatech.platform.community.domain.repositories.CommentRepository;
import greendream.cultivatech.platform.community.domain.repositories.CommunityProfileRepository;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommunityService {
    private final CommunityProfileRepository profiles;
    private final CommentRepository comments;

    public CommunityService(CommunityProfileRepository profiles, CommentRepository comments) {
        this.profiles = profiles;
        this.comments = comments;
    }

    @Transactional(readOnly = true) public List<CommunityProfile> findProfiles(Long profileId) {
        return profileId == null ? profiles.findAll() : profiles.findByProfileId(profileId).stream().toList();
    }
    @Transactional(readOnly = true) public CommunityProfile findProfile(Long id) {
        return profiles.findById(id).orElseThrow(() -> new NotFoundException("error.community.profile.not-found"));
    }
    public CommunityProfile createProfile(Long profileId, String nickname, Integer reputationScore, String bio, Integer visibility) {
        if (profiles.findByProfileId(profileId).isPresent()) throw new ConflictException("error.community.profile.already-exists");
        return profiles.save(new CommunityProfile(profileId, nickname, score(reputationScore), bio, status(visibility)));
    }
    public CommunityProfile updateProfile(Long id, String nickname, Integer reputationScore, String bio, Integer visibility) {
        CommunityProfile p = findProfile(id); p.update(nickname, score(reputationScore), bio, status(visibility)); return profiles.update(p);
    }
    public void deleteProfile(Long id) { findProfile(id); profiles.deleteById(id); }

    @Transactional(readOnly = true) public List<Comment> findComments(Long targetProfileId, Long fieldId, Long productId) {
        if (targetProfileId != null) return comments.findByTargetProfileId(targetProfileId);
        if (fieldId != null) return comments.findByFieldId(fieldId);
        if (productId != null) return comments.findByProductId(productId);
        return comments.findAll();
    }
    @Transactional(readOnly = true) public Comment findComment(Long id) {
        return comments.findById(id).orElseThrow(() -> new NotFoundException("error.community.comment.not-found"));
    }
    public Comment createComment(Long author, String type, Long target, Long field, Long product, String content, Integer rating) {
        return comments.save(new Comment(author, type, target, field, product, content, rating == null ? 0 : rating));
    }
    public Comment updateComment(Long id, String content, Integer rating) {
        Comment c = findComment(id); c.update(content, rating == null ? 0 : rating); return comments.update(c);
    }
    public void deleteComment(Long id) { findComment(id); comments.deleteById(id); }

    private int score(Integer score) { return score == null ? 0 : score; }
    private int status(Integer status) { return status == null ? 0 : status; }
}
