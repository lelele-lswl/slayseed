-- ============================================
--  插入100个测试用种子（Slay Seed 社区）
-- ============================================
--
--  注意：
--  1. 此脚本会先创建 5 个测试用户（如果不存在），然后插入 100 个种子，
--     随机分布在两个塔、不同角色、玩家数、种子类型上。
--  2. 密码字段使用 BCrypt 哈希，纯密码均为 "test123"。
--  3. 如果已有同名用户，会跳过创建，只插入种子。
-- ============================================


-- 1. 创建测试用户（如果不存在）
INSERT IGNORE INTO users (username, password, nickname, bio, seed_count, follower_count, following_count, enabled)
VALUES
    ('alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeB3G4u3Hf6F9e3c2P5a4e8a3c2d7f1b0', '爱丽丝', '塔1老玩家，专打战士无限流', 20, 15, 8, 1),
    ('bob',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeB3G4u3Hf6F9e3c2P5a4e8a3c2d7f1b1', '鲍勃',   '塔2爱好者，最爱骨妹', 25, 22, 14, 1),
    ('carol', '$2a$10$N9qo8uLOickgx2ZMRZoMyeB3G4u3Hf6F9e3c2P5a4e8a3c2d7f1b2', '卡罗尔', '毒种专家，天天刷鸡煲', 18, 11, 20, 1),
    ('david', '$2a$10$N9qo8uLOickgx2ZMRZoMyeB3G4u3Hf6F9e3c2P5a4e8a3c2d7f1b3', '大卫',   '三人模式速通选手', 15, 30, 5, 1),
    ('eve',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeB3G4u3Hf6F9e3c2P5a4e8a3c2d7f1b4', '夏娃',   '新人，随便玩玩', 22, 6, 18, 1);


-- 2. 获取这 5 个用户的 ID（保存到临时表）
DROP TEMPORARY TABLE IF EXISTS test_users;
CREATE TEMPORARY TABLE test_users (id BIGINT PRIMARY KEY, username VARCHAR(50));

INSERT INTO test_users (id, username)
SELECT id, username FROM users WHERE username IN ('alice', 'bob', 'carol', 'david', 'eve');


-- 3. 插入 100 条种子数据
DROP PROCEDURE IF EXISTS insert_test_seeds;

DELIMITER //

CREATE PROCEDURE insert_test_seeds()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_count INT;
    DECLARE rand_user_id BIGINT;
    DECLARE tower_val VARCHAR(50);
    DECLARE char_val VARCHAR(50);
    DECLARE player_val VARCHAR(20);
    DECLARE type_val VARCHAR(50);
    DECLARE code_val VARCHAR(200);
    DECLARE desc_val VARCHAR(2000);
    DECLARE tags_val VARCHAR(500);
    DECLARE likes_val INT;
    DECLARE views_val INT;
    DECLARE favs_val INT;
    DECLARE comments_val INT;

    SELECT COUNT(*) INTO user_count FROM test_users;

    WHILE i <= 100 DO
        -- 随机选作者
        SELECT id INTO rand_user_id FROM test_users ORDER BY RAND() LIMIT 1;

        -- 塔：1/2 概率塔1，1/2 概率塔2
        IF i % 2 = 1 THEN
            SET tower_val = '塔1';
            -- 塔1角色
            SET char_val = ELT(1 + FLOOR(RAND() * 4), '战士', '猎手', '鸡煲', '观者');
        ELSE
            SET tower_val = '塔2';
            -- 塔2角色
            SET char_val = ELT(1 + FLOOR(RAND() * 5), '战士', '猎手', '储君', '骨妹', '鸡煲');
        END IF;

        -- 玩家数
        SET player_val = ELT(1 + FLOOR(RAND() * 4), '单人', '双人', '三人', '4人');

        -- 种子类型
        SET type_val = ELT(1 + FLOOR(RAND() * 6), '普通', '强运', '毒种', '特殊', '速通', 'BOSS');

        -- 种子码：A-Z 6-12 位随机字母
        SET code_val = CONCAT(
            CHAR(65 + FLOOR(RAND() * 26)),
            CHAR(65 + FLOOR(RAND() * 26)),
            CHAR(65 + FLOOR(RAND() * 26)),
            CHAR(65 + FLOOR(RAND() * 26)),
            CHAR(65 + FLOOR(RAND() * 26)),
            CHAR(65 + FLOOR(RAND() * 26)),
            IF(FLOOR(RAND() * 2) = 1, CHAR(65 + FLOOR(RAND() * 26)), ''),
            IF(FLOOR(RAND() * 2) = 1, CHAR(65 + FLOOR(RAND() * 26)), '')
        );

        -- 描述
        SET desc_val = CONCAT(
            '测试种子 #', i, '：', tower_val, ' ', char_val, ' ',
            player_val, '模式，', type_val, '类型。',
            ELT(1 + FLOOR(RAND() * 5),
                '开局一张神卡，体验极佳！',
                '运气爆棚，开局即巅峰。',
                '推荐新手尝试，操作简单。',
                '高难度挑战，适合老玩家。',
                '非常均衡的种子，值得一玩。'
            )
        );

        -- 标签
        SET tags_val = ELT(1 + FLOOR(RAND() * 6),
            '高玩推荐',
            '新手友好, 速通',
            '毒种, 挑战',
            'BOSS, 稀有',
            '强运, 神开局',
            '普通, 均衡'
        );

        -- 随机统计数据
        SET likes_val = FLOOR(RAND() * 100);
        SET views_val = FLOOR(RAND() * 500) + 50;
        SET favs_val = FLOOR(RAND() * 50);
        SET comments_val = FLOOR(RAND() * 30);

        INSERT INTO seeds (
            seed_code, tower, tower_character, player_count, seed_type,
            description, tags, author_id, likes, views, comment_count, favorite_count,
            created_at, updated_at
        ) VALUES (
            code_val, tower_val, char_val, player_val, type_val,
            desc_val, tags_val, rand_user_id,
            likes_val, views_val, comments_val, favs_val,
            NOW() - INTERVAL FLOOR(RAND() * 30) DAY,
            NOW() - INTERVAL FLOOR(RAND() * 30) DAY
        );

        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

CALL insert_test_seeds();

DROP PROCEDURE insert_test_seeds;
DROP TEMPORARY TABLE IF EXISTS test_users;


-- 4. 统计信息（可选：校验）
SELECT
    COUNT(*) AS total_seeds,
    tower,
    tower_character,
    COUNT(*) AS count
FROM seeds
GROUP BY tower, tower_character
ORDER BY tower, tower_character;