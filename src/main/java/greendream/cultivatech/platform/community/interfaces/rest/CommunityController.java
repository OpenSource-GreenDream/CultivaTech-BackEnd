package greendream.cultivatech.platform.community.interfaces.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import greendream.cultivatech.platform.community.application.services.CommunityService;
import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunityController {
    private final CommunityService service;
    public CommunityController(CommunityService service) { this.service = service; }

    @GetMapping("/api/v1/community-profiles")
    public List<ProfileResource> profiles(@RequestParam(required = false, name = "profile_id") Long profileId) {
        return service.findProfiles(profileId).stream().map(ProfileResource::from).toList();
    }
    @GetMapping("/api/v1/community-profiles/{id}") public ProfileResource profile(@PathVariable Long id) { return ProfileResource.from(service.findProfile(id)); }
    @PostMapping(value = "/api/v1/community-profiles", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED) public ProfileResource createProfile(@Valid @RequestBody ProfileRequest r) {
        return ProfileResource.from(service.createProfile(r.profileId(), r.nickname(), r.reputationScore(), r.publicBio(), r.visibilityStatus()));
    }
    @PutMapping(value = "/api/v1/community-profiles/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProfileResource updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileRequest r) {
        return ProfileResource.from(service.updateProfile(id, r.nickname(), r.reputationScore(), r.publicBio(), r.visibilityStatus()));
    }
    @DeleteMapping("/api/v1/community-profiles/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable Long id) { service.deleteProfile(id); }

    @GetMapping("/api/v1/comments") public List<CommentResource> comments(
            @RequestParam(required = false, name = "target_profile_id") Long targetProfileId,
            @RequestParam(required = false, name = "field_id") Long fieldId,
            @RequestParam(required = false, name = "product_id") Long productId) {
        return service.findComments(targetProfileId, fieldId, productId).stream().map(CommentResource::from).toList();
    }
    @GetMapping("/api/v1/comments/{id}") public CommentResource comment(@PathVariable Long id) { return CommentResource.from(service.findComment(id)); }
    @PostMapping(value = "/api/v1/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED) public CommentResource createComment(@Valid @RequestBody CommentRequest r) {
        return CommentResource.from(service.createComment(r.authorProfileId(), r.targetType(), r.targetProfileId(), r.fieldId(), r.productId(), r.content(), r.rating()));
    }
    @PutMapping(value = "/api/v1/comments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentResource updateComment(@PathVariable Long id, @Valid @RequestBody UpdateCommentRequest r) {
        return CommentResource.from(service.updateComment(id, r.content(), r.rating()));
    }
    @DeleteMapping("/api/v1/comments/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) { service.deleteComment(id); }

    public record ProfileRequest(@NotNull @JsonProperty("profile_id") Long profileId, @NotBlank @Size(max=100) String nickname,
            @JsonProperty("reputation_score") Integer reputationScore, @JsonProperty("public_bio") @Size(max=500) String publicBio,
            @JsonProperty("visibility_status") Integer visibilityStatus) { }
    public record CommentRequest(@NotNull @JsonProperty("author_profile_id") Long authorProfileId,
            @JsonProperty("target_type") String targetType, @JsonProperty("target_profile_id") Long targetProfileId,
            @JsonProperty("field_id") Long fieldId, @JsonProperty("product_id") Long productId,
            @NotBlank @Size(max=1000) String content, @NotNull Integer rating) { }
    public record UpdateCommentRequest(@NotBlank @Size(max=1000) String content, @NotNull Integer rating) { }
    public record ProfileResource(Long id, @JsonProperty("profile_id") Long profileId, String nickname,
            @JsonProperty("reputation_score") int reputationScore, @JsonProperty("public_bio") String publicBio,
            @JsonProperty("visibility_status") int visibilityStatus) {
        static ProfileResource from(CommunityProfile p) { return new ProfileResource(p.getId(), p.getProfileId(), p.getNickname(), p.getReputationScore(), p.getPublicBio(), p.getVisibilityStatus()); }
    }
    public record CommentResource(Long id, @JsonProperty("author_profile_id") Long authorProfileId,
            @JsonProperty("target_type") String targetType, @JsonProperty("target_profile_id") Long targetProfileId,
            @JsonProperty("field_id") Long fieldId, @JsonProperty("product_id") Long productId, String content, int rating,
            @JsonProperty("created_at") Date createdAt, @JsonProperty("updated_at") Date updatedAt) {
        static CommentResource from(Comment c) { return new CommentResource(c.getId(), c.getAuthorProfileId(), c.getTargetType(), c.getTargetProfileId(), c.getFieldId(), c.getProductId(), c.getContent(), c.getRating(), null, null); }
    }
}
