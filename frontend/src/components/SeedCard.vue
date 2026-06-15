<template>
  <div class="seed-card" @click="$emit('viewed', seed)">
    <div class="seed-header">
      <div class="tags-left">
        <span class="tower-tag" :style="{background: getTowerColor(seed.tower)}">{{ seed.tower }}</span>
        <span class="character-tag">{{ seed.towerCharacter }}</span>
        <span v-if="seed.playerCount" class="type-tag">{{ seed.playerCount }}</span>
        <span v-if="seed.seedType" class="type-tag special">{{ seed.seedType }}</span>
      </div>
    </div>
    <div class="seed-code">{{ seed.seedCode }}</div>
    <p class="description">{{ seed.description }}</p>
    <div v-if="seed.tags" class="tags">
      <span v-for="tag in seed.tags.split(',')" :key="tag" class="tag">{{ tag.trim() }}</span>
    </div>
    <div class="seed-footer">
      <div class="stats">
        <span>👍 {{ seed.likes }}</span>
        <span>⭐ {{ seed.favoriteCount || 0 }}</span>
        <span>💬 {{ seed.commentCount || 0 }}</span>
        <span>👁️ {{ seed.views }}</span>
      </div>
    </div>
    <div class="bottom-row">
      <span v-if="seed.authorUsername" class="author-text" @click.stop="$emit('author-clicked', seed.authorId)">
        @{{ seed.authorNickname || seed.authorUsername }}
      </span>
      <span class="date-text">{{ formatDate(seed.createdAt) }}</span>
    </div>
    <div class="action-buttons" @click.stop>
      <button :class="['like-btn', {active: seed.likedByCurrentUser}]" @click="toggleLike">
        {{ seed.likedByCurrentUser ? '❤️' : '👍' }} 点赞
      </button>
      <button :class="['fav-btn', {active: seed.favoritedByCurrentUser}]" @click="toggleFavorite">
        {{ seed.favoritedByCurrentUser ? '⭐' : '☆' }} 收藏
      </button>
    </div>
  </div>
</template>

<script setup>
import { auth, seedsApi } from '../api'

const props = defineProps(['seed'])
const emit = defineEmits(['liked', 'favorited', 'viewed', 'author-clicked'])

const getTowerColor = (tower) => tower === '塔2' ? '#9b59b6' : '#3498db'
const formatDate = (date) => new Date(date).toLocaleDateString('zh-CN')

const toggleLike = () => {
  if (!auth.isLoggedIn()) { alert('请先登录'); return }
  seedsApi.likeSeed(props.seed.id).then(res => {
    emit('liked', res.liked)
  }).catch(err => alert('操作失败: ' + (err.response?.data?.error || err.message)))
}

const toggleFavorite = () => {
  if (!auth.isLoggedIn()) { alert('请先登录'); return }
  seedsApi.favoriteSeed(props.seed.id).then(res => {
    emit('favorited', res.favorited)
  }).catch(err => alert('操作失败: ' + (err.response?.data?.error || err.message)))
}
</script>

<style scoped>
.seed-card {
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}
.seed-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
}
.seed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.tags-left {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.tower-tag, .character-tag, .type-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  color: #fff;
}
.tower-tag { background: #3498db; font-weight: bold; }
.character-tag { background: rgba(0, 212, 255, 0.2); color: #00d4ff; border: 1px solid #00d4ff; }
.type-tag { background: rgba(155, 89, 182, 0.3); color: #bb88dd; border: 1px solid #9b59b6; }
.type-tag.special { background: rgba(231, 76, 60, 0.3); color: #ff9999; border-color: #e74c3c; }

.seed-code {
  font-family: monospace;
  font-size: 1.15rem;
  color: #00d4ff;
  background: rgba(0, 212, 255, 0.08);
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 12px;
  word-break: break-all;
}
.description {
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 12px;
  font-size: 0.95rem;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}
.tag {
  background: rgba(255, 255, 255, 0.08);
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 0.78rem;
  color: #aaa;
}
.seed-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.stats {
  display: flex;
  gap: 15px;
  color: #888;
  font-size: 0.85rem;
}
.bottom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0 0;
  font-size: 0.8rem;
}
.author-text {
  color: #00d4ff;
  cursor: pointer;
  font-weight: bold;
}
.author-text:hover { text-decoration: underline; }
.date-text { color: #666; }

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.like-btn, .fav-btn {
  flex: 1;
  padding: 8px 15px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  color: #00d4ff;
  cursor: pointer;
  font-size: 0.88rem;
  transition: all 0.3s;
}
.like-btn:hover, .fav-btn:hover {
  background: rgba(0, 212, 255, 0.2);
}
.like-btn.active {
  background: rgba(231, 76, 60, 0.2);
  border-color: #e74c3c;
  color: #ff8888;
}
.fav-btn.active {
  background: rgba(241, 196, 15, 0.2);
  border-color: #f1c40f;
  color: #f1c40f;
}
</style>