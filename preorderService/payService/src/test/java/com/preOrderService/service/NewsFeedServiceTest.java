//package com.preOrderService.service;
//
//import com.preOrderService.entity.Member;
//import com.preOrderService.newsFeed.dto.CommentsDto;
//import com.preOrderService.newsFeed.dto.FeedsDto;
//import com.preOrderService.dto.JoinDto;
//import com.preOrderService.newsFeed.dto.PostsDto;
//import com.preOrderService.newsFeed.service.NewsFeedService;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//@ActiveProfiles("test")
//class NewsFeedServiceTest {
//    @Autowired
//    NewsFeedService newsFeedService;
//    @Autowired
//    FollowsRepository followRepository;
//    @Autowired
//    ActivitiesRepository activitiesRepository;
//    @Autowired
//    static MemberService joinService;
//    @Autowired
//    MemberService memberService;
//    @Autowired
//    PostsRepository postsRepository;
//    @Autowired
//    CommentsRepository commentsRepository;
//    @Autowired
//    PostLikesRepository postLikesRepository;
//    @Autowired
//    CommentLikesRepository commentLikesRepository;
//
//    @BeforeAll
//    public static void init(@Autowired ApplicationContext context) {
//        joinService = context.getBean(MemberService.class);
//        JoinDto joinDto1 = new JoinDto();
//        joinDto1.setName("율이");
//        joinDto1.setRole("USER");
//        joinDto1.setPassword("123");
//        joinDto1.setEmail("aaa@aaa");
//
//        JoinDto joinDto2 = new JoinDto();
//        joinDto2.setName("태규");
//        joinDto2.setRole("USER");
//        joinDto2.setPassword("123");
//        joinDto2.setEmail("bbb@aaa");
//
//        JoinDto joinDto3 = new JoinDto();
//        joinDto3.setName("준석");
//        joinDto3.setRole("USER");
//        joinDto3.setPassword("123");
//        joinDto3.setEmail("ccc@aaa");
//
//        joinService.join(joinDto1);
//        joinService.join(joinDto2);
//        joinService.join(joinDto3);
//    }
//
//    @Test
//    public void 팔로우() {
//        //given
//        Member fromMember = memberService.findMemberByEmail("aaa@aaa");
//        Member toMember = memberService.findMemberByEmail("bbb@aaa");
//
//        //when
//        String message = newsFeedService.changeFollow("aaa@aaa", "bbb@aaa");
//        newsFeedService.changeFollow("bbb@aaa", "ccc@aaa");
//
//        //then
//        ///follow 메세지 리턴
//        List<Long> followLists = followRepository.findFollowLists(fromMember.getId());
//        assertThat(followLists.get(0)).isEqualTo(toMember.getId());
//        assertThat(message).isEqualTo("aaa@aaa 님이 bbb@aaa 님을 팔로우 했습니다.");
//
//        ///bbb의 활동이 aaa에게 뜨는지 확인
//        Member aaa = memberService.findMemberByEmail("aaa@aaa");
//
//        assertThat(
//                activitiesRepository.findByOwnerId(aaa.getId())
//                        .get(0)
//                        .getNotification()
//        ).isEqualTo("bbb@aaa" + "님이 " + "ccc@aaa" + "을 팔로우 했습니다.");
//    }
//
//    @Test
//    public void 포스트_작성() {
//        //given
//        //팔로우 aaa->bbb
//        newsFeedService.changeFollow("aaa@aaa", "bbb@aaa");
//
//        //bbb가 post 작성
//        PostsDto postsDto = new PostsDto("포스트를 작성했습니다.", null);
//        newsFeedService.writePost("bbb@aaa", postsDto);
//
//        //then
//        // 본인 피드 확인
//        List<FeedsDto> feeds1 = newsFeedService.getFeeds("bbb@aaa");
//        assertThat(feeds1.get(0).getText()).isEqualTo("포스트를 작성했습니다.");
//
//        // 팔로워 피드 확인
//        List<FeedsDto> feeds2 = newsFeedService.getFeeds("aaa@aaa");
//        assertThat(feeds2.get(0).getText()).isEqualTo("포스트를 작성했습니다.");
//
//        // 팔로우한 사람의 활동 확인
//        Member member = memberService.findMemberByEmail("aaa@aaa");
//        List<Activities> activities = activitiesRepository.findByOwnerId(member.getId());
//        assertThat(activities.get(0).getNotification()).isEqualTo("bbb@aaa님이 게시물을 작성 했습니다.");
//    }
//
//    @Test
//    public void 댓글_작성_조회() {
//
//        //given
//        newsFeedService.changeFollow("aaa@aaa", "bbb@aaa");
//        PostsDto postsDto = new PostsDto("포스트를 작성했습니다.", null);
//        newsFeedService.writePost("ccc@aaa", postsDto);
//
//        Member member = memberService.findMemberByEmail("ccc@aaa");
//        List<Posts> posts = postsRepository.findPostsByWriterId(member.getId());
//        Long postId = posts.get(0).getId();
//
//        //when
//        newsFeedService.writeComments("bbb@aaa", postId, new CommentsDto("댓글을 작성했다."));
//
//        //then
//        List<Comments> comments = commentsRepository.findByPostId(postId);
//        assertThat(comments.get(0).getText()).isEqualTo("댓글을 작성했다.");
//        assertThat(comments.get(0).getWriter().getEmail()).isEqualTo("bbb@aaa");
//
//        //팔로워의 활동에 추가 여부 확인
//        Member follower = memberService.findMemberByEmail("aaa@aaa");
//        List<Activities> followerActivities = activitiesRepository.findByOwnerId(follower.getId());
//        assertThat(followerActivities.get(0).getNotification()).isEqualTo("bbb@aaa님이 ccc@aaa님의 글에 댓글을 작성했습니다.");
//
//        //게시물 오너의 활동에 추가 여부 확인
//        Member owner = memberService.findMemberByEmail("ccc@aaa");
//        List<Activities> ownerActivities = activitiesRepository.findByOwnerId(owner.getId());
//        assertThat(ownerActivities.get(0).getNotification()).isEqualTo("bbb@aaa님이 내 게시물에 댓글을 달았습니다.");
//
//        //게시물 댓글 조회
//        newsFeedService.writeComments("aaa@aaa", postId, new CommentsDto("두번째 댓글을 작성했다."));
//        List<Comments> comments1 = commentsRepository.findByPostId(postId);
//        for (Comments comment : comments1) {
//            System.out.println("comment.getText() = " + comment.getText());
//        }
//    }
//
//    @Test
//    public void 게시글_좋아요() {
//        //given
//        newsFeedService.changeFollow("aaa@aaa", "bbb@aaa");
//        newsFeedService.changeFollow("bbb@aaa", "ccc@aaa");
//
//        PostsDto postsDto = new PostsDto("포스트를 작성했습니다.", null);
//        newsFeedService.writePost("ccc@aaa", postsDto);
//
//        Member ccc = memberService.findMemberByEmail("ccc@aaa");
//        List<Posts> posts = postsRepository.findPostsByWriterId(ccc.getId());
//
//        Member aaa = memberService.findMemberByEmail("aaa@aaa");
//
//        //when
//        newsFeedService.postLike("bbb@aaa", posts.get(0).getId());
//
//        //then
//        PostLikes postLike = postLikesRepository.findByPostId(posts.get(0).getId()).get(0);
//        assertThat(postLike.getLikers().getEmail()).isEqualTo("bbb@aaa");
//
//        List<Activities> activities = activitiesRepository.findByOwnerId(aaa.getId());
//        for(Activities activity : activities){
//            System.out.println(activity.getNotification());
//        }
//        assertThat(activities.get(0).getNotification()).isEqualTo("bbb@aaa님이 ccc@aaa님의 글을 좋아합니다.");
//
//        List<Activities> activities2 = activitiesRepository.findByOwnerId(ccc.getId());
//        for(Activities activity : activities2){
//            System.out.println(activity.getNotification());
//        }
//        assertThat(activities2.get(0).getNotification()).isEqualTo("bbb@aaa님이 내 게시글을 좋아합니다.");
//    }
//
//    @Test
//    public void 댓글_좋아요() {
//        //given
//        newsFeedService.changeFollow("aaa@aaa", "bbb@aaa");
//        newsFeedService.changeFollow("bbb@aaa", "ccc@aaa");
//        newsFeedService.changeFollow("ccc@aaa", "aaa@aaa");
//
//        PostsDto postsDto = new PostsDto("포스트를 작성했습니다.", null);
//        newsFeedService.writePost("ccc@aaa", postsDto);
//
//        Member ccc = memberService.findMemberByEmail("ccc@aaa");
//        Member bbb = memberService.findMemberByEmail("bbb@aaa");
//
//        List<Posts> posts = postsRepository.findPostsByWriterId(ccc.getId());
//
//        Long commentid = newsFeedService.writeComments("bbb@aaa",posts.get(0).getId(),new CommentsDto("댓글을 작성했습니다."));
//
//        //when
//        newsFeedService.commentLike(commentid,"aaa@aaa");
//
//        //then
//        Optional<Comments> comment = commentsRepository.findById(commentid);
//        CommentLikes commentLikes = commentLikesRepository.findCommentLikesByLikeUsersEmail("aaa@aaa");
//        assertThat(commentLikes.getComments().getWriter().getEmail()).isEqualTo("bbb@aaa");
//        assertThat(commentLikes.getComments().getText()).isEqualTo("댓글을 작성했습니다.");
//
//        List<Activities> activities = activitiesRepository.findByOwnerId(ccc.getId());
//        for(Activities a: activities){
//            System.out.println(a.getNotification());
//        }
//    }
//}