package org.lelele.slayseed.config;

import org.lelele.slayseed.entity.Seed;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    private static final String[] TOWERS = {"塔1", "塔2"};
    private static final String[] TOWER1_CHARS = {"战士", "猎手", "鸡煲", "观者"};
    private static final String[] TOWER2_CHARS = {"战士", "猎手", "储君", "骨妹", "鸡煲"};
    private static final String[] PLAYER_COUNTS = {"单人", "双人", null};
    private static final String[] SEED_TYPES = {"日常", "挑战", "竞速", null};
    private static final String[] DESCRIPTIONS = {
            "开局遗物极佳，第一层商店有铃铛",
            "精英房掉落稀有遗物，推荐打卡",
            "前三层无伤路线，适合新手练习",
            "Boss掉落核心卡牌，构筑流畅",
            "商店有稀有药水，运气爆棚",
            "火堆位置完美，升级节奏舒服",
            "问号房奖励丰厚，路线灵活",
            "开局拿到核心遗物，一路碾压",
            "中期转型顺滑，后期稳定输出",
            "遗物组合强力，推荐尝试",
            "第一层精英必打，掉落关键卡",
            "Boss前有商店，可以调整卡组",
            "问号房有免费移除，卡组精简",
            "药水掉落多，容错率高",
            "稀有事件触发，获得强力遗物",
            "路线选择多，适合不同打法",
            "开局遗物一般但中期发力",
            "商店有双移除，卡组极简",
            "Boss遗物选择多，构筑空间大",
            "整体节奏紧凑，推荐速通"
    };
    private static final String[] TAGS_LIST = {
            "遗物,强力", "新手友好", "速通", "高容错", "稀有遗物",
            "卡组构筑", "挑战", "日常推荐", "竞速", "无伤",
            "商店好物", "精英必打", "问号房", "药水多", "转型顺滑"
    };

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               SeedRepository seedRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (seedRepository.count() > 0) {
                return;
            }

            Random random = new Random();

            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("test123456"));
            testUser.setNickname("测试玩家");
            testUser.setSeedCount(100);
            testUser = userRepository.save(testUser);

            for (int i = 1; i <= 100; i++) {
                String tower = TOWERS[random.nextInt(TOWERS.length)];
                String[] chars = "塔1".equals(tower) ? TOWER1_CHARS : TOWER2_CHARS;
                String towerCharacter = chars[random.nextInt(chars.length)];
                String playerCount = PLAYER_COUNTS[random.nextInt(PLAYER_COUNTS.length)];
                String seedType = SEED_TYPES[random.nextInt(SEED_TYPES.length)];
                String description = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)] + " #" + i;
                String tags = TAGS_LIST[random.nextInt(TAGS_LIST.length)];

                Seed seed = new Seed();
                seed.setSeedCode(generateSeedCode(random));
                seed.setTower(tower);
                seed.setTowerCharacter(towerCharacter);
                seed.setPlayerCount(playerCount);
                seed.setSeedType(seedType);
                seed.setDescription(description);
                seed.setTags(tags);
                seed.setAuthor(testUser);
                seed.setLikes(random.nextInt(50));
                seed.setViews(random.nextInt(200) + 10);
                seed.setCommentCount(random.nextInt(10));
                seed.setFavoriteCount(random.nextInt(20));

                seedRepository.save(seed);
            }

            System.out.println("✅ 已插入100条测试种子数据，测试账号: testuser / test123456");
        };
    }

    private String generateSeedCode(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}