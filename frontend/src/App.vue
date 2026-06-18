<template>
  <div class="app">
    <header class="top-header">
      <div class="logo" @click="goHome">
        <span class="logo-icon">🎮</span>
        <span class="logo-text">杀戮尖塔种子分享</span>
      </div>
      <div class="search-bar" v-if="currentView === 'home'">
        <input v-model="searchKeyword" @keyup.enter="doSearch" placeholder="搜索种子码、描述、标签..." />
        <button @click="doSearch">🔍</button>
      </div>
      <nav class="user-nav">
        <button v-if="!currentUser" @click="showLogin = true" class="nav-btn">登录</button>
        <button v-if="!currentUser" @click="showRegister = true" class="nav-btn primary">注册</button>
        <template v-else>
          <button @click="goProfile(currentUser.id)" class="user-btn">
            <span class="avatar-sm">{{ currentUser.nickname ? currentUser.nickname.charAt(0) : currentUser.username.charAt(0) }}</span>
            <span>{{ currentUser.nickname || currentUser.username }}</span>
          </button>
          <button @click="logout" class="nav-btn">退出</button>
        </template>
      </nav>
    </header>

    <!-- 登录弹窗 -->
    <div v-if="showLogin" class="modal-overlay" @click.self="showLogin = false">
      <div class="modal">
        <h2>登录</h2>
        <input v-model="loginForm.username" placeholder="用户名" />
        <input v-model="loginForm.password" type="password" placeholder="密码" @keyup.enter="doLogin" />
        <p v-if="loginError" class="error-msg">{{ loginError }}</p>
        <div class="modal-actions">
          <button @click="showLogin = false" class="btn-secondary">取消</button>
          <button @click="doLogin" class="btn-primary">登录</button>
        </div>
        <p class="switch-text">还没有账号？ <a @click="showLogin = false; showRegister = true">立即注册</a></p>
      </div>
    </div>

    <!-- 注册弹窗 -->
    <div v-if="showRegister" class="modal-overlay" @click.self="showRegister = false">
      <div class="modal">
        <h2>注册</h2>
        <input v-model="registerForm.username" placeholder="用户名（3-50字符）" />
        <input v-model="registerForm.password" type="password" placeholder="密码（至少6位）" />
        <input v-model="registerForm.nickname" placeholder="昵称（可选）" />
        <p v-if="registerError" class="error-msg">{{ registerError }}</p>
        <div class="modal-actions">
          <button @click="showRegister = false" class="btn-secondary">取消</button>
          <button @click="doRegister" class="btn-primary">注册</button>
        </div>
        <p class="switch-text">已有账号？ <a @click="showRegister = false; showLogin = true">立即登录</a></p>
      </div>
    </div>

    <!-- 主内容 -->
    <main v-if="currentView === 'home'" class="main-content">
      <!-- 分类筛选 -->
      <section class="filter-section">
        <div class="filter-row">
          <span class="filter-label">塔：</span>
          <button v-for="t in TOWER_OPTIONS" :key="t.value"
                  :class="{active: filters.tower === t.value}"
                  @click="setFilter('tower', t.value)">
            {{ t.label }}
          </button>
        </div>
        <div class="filter-row">
          <span class="filter-label">角色：</span>
          <button v-for="c in currentCharacterOptions" :key="c.value"
                  :class="{active: filters.towerCharacter === c.value}"
                  @click="setFilter('towerCharacter', c.value)">
            {{ c.label }}
          </button>
        </div>
        <div class="filter-row">
          <span class="filter-label">人数：</span>
          <button v-for="p in PLAYER_OPTIONS" :key="p.value"
                  :class="{active: filters.playerCount === p.value}"
                  @click="setFilter('playerCount', p.value)">
            {{ p.label }}
          </button>
        </div>
        <div class="filter-row">
          <span class="filter-label">类型：</span>
          <button v-for="s in SEED_TYPE_OPTIONS" :key="s.value"
                  :class="{active: filters.seedType === s.value}"
                  @click="setFilter('seedType', s.value)">
            {{ s.label }}
          </button>
        </div>
        <div class="filter-row">
          <span class="filter-label">排序：</span>
          <button :class="{active: filters.sortBy === 'createdAt'}" @click="setFilter('sortBy', 'createdAt')">最新</button>
          <button :class="{active: filters.sortBy === 'likes'}" @click="setFilter('sortBy', 'likes')">热门</button>
          <button @click="clearFilters" class="clear-btn">清除筛选</button>
        </div>
      </section>

      <!-- 发布种子 -->
      <section class="create-section">
        <button class="toggle-create-btn" @click="showCreateForm = !showCreateForm">
          {{ showCreateForm ? '收起' : '✚ 分享新种子' }}
        </button>
        <div v-if="showCreateForm" class="create-form">
          <div class="form-row">
            <label>种子码*</label>
            <input v-model="seedForm.seedCode" placeholder="输入种子码" />
          </div>
          <div class="form-row-two">
            <div class="form-row">
              <label>塔*</label>
              <select v-model="seedForm.tower" @change="seedForm.towerCharacter = ''">
                <option v-for="t in TOWER_OPTIONS" :key="t.value" :value="t.value">{{ t.label }}</option>
              </select>
            </div>
            <div class="form-row">
              <label>角色*</label>
              <select v-model="seedForm.towerCharacter">
                <option value="">选择角色</option>
                <option v-for="c in characterOptionsForForm" :key="c.value" :value="c.value">{{ c.label }}</option>
              </select>
            </div>
          </div>
          <div class="form-row-two">
            <div class="form-row">
              <label>人数</label>
              <select v-model="seedForm.playerCount">
                <option value="">不限</option>
                <option v-for="p in PLAYER_OPTIONS.filter(o => o.value)" :key="p.value" :value="p.value">{{ p.label }}</option>
              </select>
            </div>
            <div class="form-row">
              <label>类型</label>
              <select v-model="seedForm.seedType">
                <option value="">不限</option>
                <option v-for="s in SEED_TYPE_OPTIONS.filter(o => o.value)" :key="s.value" :value="s.value">{{ s.label }}</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <label>描述*</label>
            <textarea v-model="seedForm.description" rows="3" placeholder="描述这个种子的特点..."></textarea>
          </div>
          <div class="form-row">
            <label>标签</label>
            <input v-model="seedForm.tags" placeholder="用逗号分隔，如：好开局,多遗物,简单" />
          </div>
          <div class="form-actions">
            <button @click="showCreateForm = false" class="btn-secondary">取消</button>
            <button @click="submitSeed" class="btn-primary">提交分享</button>
          </div>
        </div>
      </section>

      <!-- 种子列表 -->
      <section class="seeds-grid">
        <div v-if="seedsLoading" class="loading">加载中...</div>
        <div v-else-if="!seedList || seedList.length === 0" class="empty">暂无种子，快去分享第一个吧！</div>
        <SeedCard v-for="seed in seedList" :key="seed.id" :seed="seed"
                  @liked="handleSeedLiked(seed, $event)"
                  @favorited="handleSeedFavorited(seed, $event)"
                  @viewed="viewSeed(seed)"
                  @author-clicked="goProfile" />
      </section>

      <div v-if="totalPages > 1" class="pagination">
        <button @click="prevPage" :disabled="page <= 0">上一页</button>
        <span>第 {{ page + 1 }} / {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="page >= totalPages - 1">下一页</button>
      </div>
    </main>

    <!-- 种子详情 -->
    <main v-else-if="currentView === 'seedDetail'" class="main-content detail-view">
      <button class="back-btn" @click="goHome">← 返回列表</button>
      <div v-if="currentSeed" class="seed-detail">
        <div class="detail-header">
          <div class="tags-row">
            <span class="tower-tag" :style="{background: getTowerColor(currentSeed.tower)}">{{ getTowerLabel(currentSeed.tower) }}</span>
            <span class="character-tag">{{ currentSeed.towerCharacter }}</span>
            <span v-if="currentSeed.playerCount" class="type-tag">{{ currentSeed.playerCount }}</span>
            <span v-if="currentSeed.seedType" class="type-tag special">{{ currentSeed.seedType }}</span>
          </div>
          <h2 class="seed-code-display">{{ currentSeed.seedCode }}</h2>
          <p class="seed-description">{{ currentSeed.description }}</p>
          <p v-if="currentSeed.tags" class="seed-tags">标签：{{ currentSeed.tags }}</p>
          <div class="author-row">
            <span>作者：</span>
            <a class="author-link" @click="goProfile(currentSeed.authorId)">{{ currentSeed.authorNickname || currentSeed.authorUsername }}</a>
            <span class="date-text">{{ formatDate(currentSeed.createdAt) }}</span>
          </div>
          <div class="stats-row">
            <span>👍 {{ currentSeed.likes }}</span>
            <span>⭐ {{ currentSeed.favoriteCount || 0 }}</span>
            <span>💬 {{ currentSeed.commentCount || 0 }}</span>
            <span>👁️ {{ currentSeed.views }}</span>
          </div>
          <div class="action-row">
            <button :class="{active: currentSeed.likedByCurrentUser, 'btn-primary': true}" @click="doLikeDetail">
              {{ currentSeed.likedByCurrentUser ? '❤️ 已点赞' : '👍 点赞' }}
            </button>
            <button :class="{active: currentSeed.favoritedByCurrentUser}" @click="doFavoriteDetail">
              {{ currentSeed.favoritedByCurrentUser ? '⭐ 已收藏' : '☆ 收藏' }}
            </button>
            <button v-if="currentUser && currentSeed.authorId === currentUser.id"
                    @click="deleteCurrentSeed" class="btn-danger">
              删除
            </button>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h3>💬 评论 ({{ currentSeed.commentCount || 0 }})</h3>
          <div v-if="currentUser" class="comment-input">
            <textarea v-model="newComment" rows="2" placeholder="写点什么..."></textarea>
            <button @click="submitComment" class="btn-primary">发表</button>
          </div>
          <div v-else class="login-hint">请登录后参与评论</div>
          <div class="comment-list">
            <div v-if="commentsLoading" class="loading">加载评论中...</div>
            <div v-else-if="comments.length === 0" class="empty">暂无评论，快来抢沙发！</div>
            <div v-for="c in comments" :key="c.id" class="comment-item">
              <div class="comment-header">
                <span class="comment-author" @click="goProfile(c.authorId)">
                  {{ c.authorNickname || c.authorUsername }}
                </span>
                <span class="comment-date">{{ formatDate(c.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ c.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 用户主页 -->
    <main v-else-if="currentView === 'profile'" class="main-content profile-view">
      <button class="back-btn" @click="goHome">← 返回列表</button>
      <div v-if="profileUser" class="profile-header">
        <div class="profile-avatar">{{ (profileUser.nickname || profileUser.username).charAt(0) }}</div>
        <div class="profile-info">
          <h2>{{ profileUser.nickname || profileUser.username }}</h2>
          <p v-if="profileUser.bio" class="bio">{{ profileUser.bio }}</p>
          <div class="profile-stats">
            <span>📦 {{ profileUser.seedCount || 0 }} 个种子</span>
            <span>👥 {{ profileUser.followerCount || 0 }} 粉丝</span>
            <span>➡️ {{ profileUser.followingCount || 0 }} 关注</span>
          </div>
          <div v-if="currentUser && currentUser.id !== profileUser.id" class="profile-actions">
            <button :class="{'btn-primary': true, active: isFollowingProfile}" @click="toggleFollowProfile">
              {{ isFollowingProfile ? '✔️ 已关注' : '+ 关注' }}
            </button>
          </div>
        </div>
      </div>

      <div class="profile-tabs">
        <button :class="{active: profileTab === 'seeds'}" @click="profileTab = 'seeds'">TA的种子</button>
        <button :class="{active: profileTab === 'favorites'}" @click="profileTab = 'favorites'">TA的收藏</button>
      </div>

      <section class="seeds-grid">
        <div v-if="seedsLoading" class="loading">加载中...</div>
        <div v-else-if="!seedList || seedList.length === 0" class="empty">
          {{ profileTab === 'seeds' ? '还没有分享过种子' : '还没有收藏' }}
        </div>
        <SeedCard v-for="seed in seedList" :key="seed.id" :seed="seed"
                  @liked="handleSeedLiked(seed, $event)"
                  @favorited="handleSeedFavorited(seed, $event)"
                  @viewed="viewSeed(seed)"
                  @author-clicked="goProfile" />
      </section>
    </main>

    <footer class="app-footer">
      <p>© 杀戮尖塔种子分享社区 | 分享你的精彩开局</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch } from 'vue'
import SeedCard from './components/SeedCard.vue'
import { auth, seedsApi, usersApi, commentsApi } from './api'

// 常量
const TOWER_OPTIONS = [
  { label: '塔1', value: '塔1' },
  { label: '塔2', value: '塔2' }
]
const TOWER1_CHARS = [
  { label: '全部', value: '' },
  { label: '战士', value: '战士' },
  { label: '猎手', value: '猎手' },
  { label: '鸡煲', value: '鸡煲' },
  { label: '观者', value: '观者' }
]
const TOWER2_CHARS = [
  { label: '全部', value: '' },
  { label: '战士', value: '战士' },
  { label: '猎手', value: '猎手' },
  { label: '储君', value: '储君' },
  { label: '骨妹', value: '骨妹' },
  { label: '鸡煲', value: '鸡煲' }
]
const PLAYER_OPTIONS = [
  { label: '全部', value: '' },
  { label: '单人', value: '单人' },
  { label: '双人', value: '双人' },
  { label: '三人', value: '三人' },
  { label: '4人', value: '4人' }
]
const SEED_TYPE_OPTIONS = [
  { label: '全部', value: '' },
  { label: '农种', value: '农种' },
  { label: '毒种', value: '毒种' },
  { label: '特殊', value: '特殊' }
]

const currentUser = ref(auth.getCurrentUser())
const currentView = ref('home')
const showLogin = ref(false)
const showRegister = ref(false)
const showCreateForm = ref(false)
const loginError = ref('')
const registerError = ref('')
const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', nickname: '' })

const filters = reactive({
  tower: '塔2',
  towerCharacter: '',
  playerCount: '',
  seedType: '',
  sortBy: 'createdAt'
})
const searchKeyword = ref('')
const page = ref(0)
const totalPages = ref(1)
const seedList = ref([])
const seedsLoading = ref(false)

const seedForm = reactive({
  seedCode: '', tower: '塔2', towerCharacter: '',
  playerCount: '', seedType: '',
  description: '', tags: ''
})

const currentSeed = ref(null)
const comments = ref([])
const commentsLoading = ref(false)
const newComment = ref('')

const profileUser = ref(null)
const profileTab = ref('seeds')
const isFollowingProfile = ref(false)

watch(profileTab, () => {
  page.value = 0
  loadSeeds()
})

const currentCharacterOptions = computed(() => {
  if (filters.tower === '塔2') return TOWER2_CHARS
  return TOWER1_CHARS
})

const characterOptionsForForm = computed(() => {
  if (seedForm.tower === '塔2') return TOWER2_CHARS.filter(c => c.value)
  return TOWER1_CHARS.filter(c => c.value)
})

function getTowerColor(tower) {
  return tower === '塔2' ? '#9b59b6' : '#3498db'
}
function getTowerLabel(tower) { return tower || '未分类' }
function formatDate(date) { return new Date(date).toLocaleString('zh-CN') }

function goHome() {
  currentView.value = 'home'
  currentSeed.value = null
  profileUser.value = null
  page.value = 0
  loadSeeds()
}
function viewSeed(seed) {
  currentView.value = 'seedDetail'
  currentSeed.value = null
  seedsApi.getSeed(seed.id).then(data => {
    currentSeed.value = data
    loadComments(seed.id)
  }).catch(err => alert('加载失败: ' + (err.response?.data?.error || err.message)))
}
function goProfile(userId) {
  if (!userId) return
  currentView.value = 'profile'
  profileUser.value = null
  page.value = 0
  usersApi.getUser(userId).then(data => {
    profileUser.value = data
    if (currentUser.value && currentUser.value.id !== data.id) {
      usersApi.isFollowing(userId).then(f => isFollowingProfile.value = f)
    }
    loadProfileSeeds()
  })
}

function setFilter(key, value) {
  filters[key] = filters[key] === value ? '' : value
  if (key === 'tower') filters.towerCharacter = ''
  page.value = 0
  loadSeeds()
}
function clearFilters() {
  filters.tower = '塔2'
  filters.towerCharacter = ''
  filters.playerCount = ''
  filters.seedType = ''
  filters.sortBy = 'createdAt'
  searchKeyword.value = ''
  page.value = 0
  loadSeeds()
}
function doSearch() {
  if (searchKeyword.value.trim()) {
    page.value = 0
    loadSeeds()
  } else {
    loadSeeds()
  }
}

function loadSeeds() {
  seedsLoading.value = true
  const params = { page: page.value, size: 20, sortBy: filters.sortBy }
  if (filters.tower) params.tower = filters.tower
  if (filters.towerCharacter) params.towerCharacter = filters.towerCharacter
  if (filters.playerCount) params.playerCount = filters.playerCount
  if (filters.seedType) params.seedType = filters.seedType

  let promise
  if (searchKeyword.value.trim()) {
    promise = seedsApi.searchSeeds(searchKeyword.value.trim(), page.value, 20)
  } else if (currentView.value === 'profile' && profileUser.value) {
    if (profileTab.value === 'favorites') {
      promise = usersApi.getUserFavorites(profileUser.value.id, page.value, 20)
    } else {
      promise = usersApi.getUserSeeds(profileUser.value.id, page.value, 20)
    }
  } else {
    promise = seedsApi.getSeeds(params)
  }

  promise.then(data => {
    seedList.value = data.content || []
    totalPages.value = data.totalPages || 1
    seedsLoading.value = false
  }).catch(() => {
    seedList.value = []
    seedsLoading.value = false
  })
}
function loadProfileSeeds() { loadSeeds() }

function prevPage() { if (page.value > 0) { page.value--; loadSeeds() } }
function nextPage() { if (page.value < totalPages.value - 1) { page.value++; loadSeeds() } }

function handleSeedLiked(seed, liked) {
  seed.likes += liked ? 1 : -1
  seed.likedByCurrentUser = liked
}
function handleSeedFavorited(seed, favorited) {
  seed.favoritedByCurrentUser = favorited
  seed.favoriteCount = (seed.favoriteCount || 0) + (favorited ? 1 : -1)
}

function doLogin() {
  loginError.value = ''
  auth.login(loginForm.username, loginForm.password).then(data => {
    currentUser.value = data.user
    showLogin.value = false
    loginForm.username = ''
    loginForm.password = ''
    loadSeeds()
  }).catch(err => {
    loginError.value = err.response?.data?.error || '登录失败'
  })
}
function doRegister() {
  registerError.value = ''
  auth.register(registerForm.username, registerForm.password, registerForm.nickname || undefined).then(data => {
    currentUser.value = data.user
    showRegister.value = false
    registerForm.username = ''
    registerForm.password = ''
    registerForm.nickname = ''
    loadSeeds()
  }).catch(err => {
    registerError.value = err.response?.data?.error || '注册失败'
  })
}
function logout() {
  auth.logout()
  currentUser.value = null
  goHome()
}

function submitSeed() {
  if (!currentUser.value) {
    showLogin.value = true
    return
  }
  if (!seedForm.seedCode || !seedForm.tower || !seedForm.towerCharacter || !seedForm.description) {
    alert('请填写必填项（种子码、塔、角色、描述）')
    return
  }
  seedsApi.createSeed({
    seedCode: seedForm.seedCode,
    tower: seedForm.tower,
    towerCharacter: seedForm.towerCharacter,
    playerCount: seedForm.playerCount || undefined,
    seedType: seedForm.seedType || undefined,
    description: seedForm.description,
    tags: seedForm.tags || undefined
  }).then(() => {
    alert('分享成功！')
    seedForm.seedCode = ''
    seedForm.tower = '塔2'
    seedForm.towerCharacter = ''
    seedForm.playerCount = ''
    seedForm.seedType = ''
    seedForm.description = ''
    seedForm.tags = ''
    showCreateForm.value = false
    loadSeeds()
  }).catch(err => {
    alert('提交失败: ' + (err.response?.data?.error || err.message))
  })
}

function doLikeDetail() {
  if (!currentUser.value) { showLogin.value = true; return }
  seedsApi.likeSeed(currentSeed.value.id).then(res => {
    currentSeed.value.likedByCurrentUser = res.liked
    currentSeed.value.likes += res.liked ? 1 : -1
  })
}
function doFavoriteDetail() {
  if (!currentUser.value) { showLogin.value = true; return }
  seedsApi.favoriteSeed(currentSeed.value.id).then(res => {
    currentSeed.value.favoritedByCurrentUser = res.favorited
    currentSeed.value.favoriteCount = (currentSeed.value.favoriteCount || 0) + (res.favorited ? 1 : -1)
  })
}
function deleteCurrentSeed() {
  if (!confirm('确定删除此种子吗？')) return
  seedsApi.deleteSeed(currentSeed.value.id).then(() => {
    alert('删除成功')
    goHome()
  })
}

function loadComments(seedId) {
  commentsLoading.value = true
  commentsApi.getComments(seedId).then(data => {
    comments.value = data.content || []
    commentsLoading.value = false
  }).catch(() => {
    comments.value = []
    commentsLoading.value = false
  })
}
function submitComment() {
  if (!currentUser.value) { showLogin.value = true; return }
  if (!newComment.value.trim()) return
  commentsApi.createComment(currentSeed.value.id, newComment.value.trim()).then(() => {
    newComment.value = ''
    loadComments(currentSeed.value.id)
    currentSeed.value.commentCount = (currentSeed.value.commentCount || 0) + 1
  }).catch(err => alert('发表失败: ' + (err.response?.data?.error || err.message)))
}

function toggleFollowProfile() {
  if (!currentUser.value) { showLogin.value = true; return }
  usersApi.followUser(profileUser.value.id).then(res => {
    isFollowingProfile.value = res.following
    if (res.following) {
      profileUser.value.followerCount = (profileUser.value.followerCount || 0) + 1
    } else {
      profileUser.value.followerCount = (profileUser.value.followerCount || 1) - 1
    }
  })
}

onMounted(() => {
  loadSeeds()
})
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #0f0f1e;
  color: #e0e0e0;
}

.top-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px 30px;
  background: linear-gradient(135deg, #1a1a3a, #0f0f1e);
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
}
.logo { cursor: pointer; display: flex; align-items: center; gap: 8px; }
.logo-icon { font-size: 24px; }
.logo-text { font-size: 1.2rem; font-weight: bold; color: #00d4ff; }

.search-bar {
  flex: 1;
  max-width: 500px;
  display: flex;
  gap: 5px;
}
.search-bar input {
  flex: 1;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 0.95rem;
}
.search-bar input:focus { outline: none; border-color: #00d4ff; }
.search-bar button {
  padding: 10px 15px;
  background: #00d4ff;
  border: none;
  border-radius: 25px;
  color: #1a1a2e;
  cursor: pointer;
  font-size: 1rem;
}

.user-nav { display: flex; gap: 10px; align-items: center; }
.nav-btn {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid #00d4ff;
  border-radius: 20px;
  color: #00d4ff;
  cursor: pointer;
  transition: all 0.3s;
}
.nav-btn:hover { background: #00d4ff; color: #1a1a2e; }
.nav-btn.primary { background: #00d4ff; color: #1a1a2e; }
.user-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 20px;
  color: #00d4ff;
  cursor: pointer;
}
.avatar-sm {
  width: 24px; height: 24px;
  background: linear-gradient(135deg, #00d4ff, #9b59b6);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: #fff;
}

.main-content { padding: 30px; max-width: 1200px; margin: 0 auto; }

.filter-section {
  background: linear-gradient(145deg, #1a1a3a, #151530);
  padding: 20px;
  border-radius: 15px;
  margin-bottom: 25px;
}
.filter-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
}
.filter-row:last-child { margin-bottom: 0; }
.filter-label { color: #00d4ff; font-weight: bold; min-width: 50px; }
.hint-text { color: #666; font-size: 0.9rem; font-style: italic; }
.filter-row button {
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  color: #aaa;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}
.filter-row button:hover { border-color: #00d4ff; color: #00d4ff; }
.filter-row button.active {
  background: #00d4ff;
  color: #1a1a2e;
  border-color: #00d4ff;
}
.clear-btn { background: rgba(231, 76, 60, 0.2) !important; border-color: #e74c3c !important; color: #e74c3c !important; }
.clear-btn:hover { background: #e74c3c !important; color: #fff !important; }

.create-section { margin-bottom: 25px; text-align: center; }
.toggle-create-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border: none;
  border-radius: 25px;
  color: #fff;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.3s;
}
.toggle-create-btn:hover { transform: scale(1.05); }

.create-form {
  margin-top: 20px;
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  padding: 25px;
  border-radius: 15px;
  text-align: left;
}
.form-row { margin-bottom: 15px; display: flex; flex-direction: column; gap: 5px; }
.form-row label { color: #00d4ff; font-weight: bold; font-size: 0.9rem; }
.form-row input, .form-row textarea, .form-row select {
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 0.95rem;
}
.form-row input:focus, .form-row textarea:focus, .form-row select:focus {
  outline: none; border-color: #00d4ff;
}
.form-row-two { display: flex; gap: 15px; }
.form-row-two .form-row { flex: 1; }
.form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }

.btn-primary {
  padding: 10px 25px;
  background: linear-gradient(135deg, #00d4ff, #0099cc);
  border: none;
  border-radius: 8px;
  color: #1a1a2e;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0, 212, 255, 0.4); }
.btn-primary.active { background: linear-gradient(135deg, #e74c3c, #c0392b); color: #fff; }
.btn-secondary {
  padding: 10px 25px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #aaa;
  cursor: pointer;
}
.btn-secondary:hover { background: rgba(255, 255, 255, 0.2); }
.btn-danger {
  padding: 10px 25px;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border: none;
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
}

.seeds-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.loading, .empty {
  grid-column: 1/-1;
  text-align: center;
  padding: 50px;
  color: #888;
  font-size: 1.1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding: 20px;
}
.pagination button {
  padding: 8px 20px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid #00d4ff;
  border-radius: 8px;
  color: #00d4ff;
  cursor: pointer;
}
.pagination button:disabled { opacity: 0.3; cursor: not-allowed; }

.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
.modal {
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  padding: 30px;
  border-radius: 15px;
  min-width: 350px;
  max-width: 450px;
}
.modal h2 { margin: 0 0 20px 0; color: #00d4ff; text-align: center; }
.modal input {
  width: 100%;
  padding: 12px;
  margin-bottom: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #e0e0e0;
  box-sizing: border-box;
}
.modal input:focus { outline: none; border-color: #00d4ff; }
.error-msg { color: #e74c3c; font-size: 0.9rem; margin: 10px 0; }
.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 15px; }
.switch-text { text-align: center; margin-top: 15px; color: #888; font-size: 0.9rem; }
.switch-text a { color: #00d4ff; cursor: pointer; text-decoration: underline; }

.detail-view .back-btn, .profile-view .back-btn {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #aaa;
  cursor: pointer;
  margin-bottom: 20px;
}
.back-btn:hover { color: #00d4ff; border-color: #00d4ff; }

.seed-detail {
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  padding: 30px;
  border-radius: 15px;
}
.tags-row { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 15px; }
.tower-tag, .character-tag, .type-tag {
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  color: #fff;
}
.tower-tag { background: #3498db; }
.character-tag { background: rgba(0, 212, 255, 0.2); color: #00d4ff; border: 1px solid #00d4ff; }
.type-tag { background: rgba(155, 89, 182, 0.3); color: #bb88dd; border: 1px solid #9b59b6; }
.type-tag.special { background: rgba(231, 76, 60, 0.3); color: #e74c3c; border-color: #e74c3c; }
.seed-code-display {
  font-family: monospace;
  font-size: 2rem;
  color: #00d4ff;
  margin: 10px 0;
  padding: 15px;
  background: rgba(0, 212, 255, 0.05);
  border-radius: 10px;
  word-break: break-all;
}
.seed-description {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #ccc;
  margin: 15px 0;
}
.seed-tags { color: #888; font-size: 0.9rem; }
.author-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.author-link { color: #00d4ff; cursor: pointer; font-weight: bold; }
.author-link:hover { text-decoration: underline; }
.date-text { color: #666; font-size: 0.85rem; margin-left: auto; }
.stats-row { display: flex; gap: 25px; padding: 15px 0; color: #aaa; }
.action-row { display: flex; gap: 10px; margin-top: 10px; }
.action-row button.active { background: linear-gradient(135deg, #e74c3c, #c0392b); color: #fff; }

.comments-section { margin-top: 30px; }
.comments-section h3 { color: #00d4ff; margin-bottom: 15px; }
.comment-input { display: flex; gap: 10px; margin-bottom: 20px; }
.comment-input textarea {
  flex: 1;
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #e0e0e0;
  resize: vertical;
}
.login-hint { padding: 15px; text-align: center; color: #888; background: rgba(255,255,255,0.03); border-radius: 8px; margin-bottom: 20px; }
.comment-list { display: flex; flex-direction: column; gap: 15px; }
.comment-item {
  background: rgba(255, 255, 255, 0.03);
  padding: 15px;
  border-radius: 10px;
  border-left: 3px solid #00d4ff;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.comment-author { color: #00d4ff; font-weight: bold; cursor: pointer; }
.comment-date { color: #666; font-size: 0.8rem; }
.comment-content { margin: 0; color: #ccc; line-height: 1.6; }

.profile-header {
  display: flex;
  gap: 25px;
  align-items: center;
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 25px;
}
.profile-avatar {
  width: 100px; height: 100px;
  background: linear-gradient(135deg, #00d4ff, #9b59b6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: #fff;
  flex-shrink: 0;
}
.profile-info { flex: 1; }
.profile-info h2 { margin: 0 0 5px 0; color: #00d4ff; font-size: 1.8rem; }
.bio { color: #aaa; margin: 5px 0 15px 0; }
.profile-stats { display: flex; gap: 20px; color: #ccc; margin-bottom: 15px; }
.profile-actions { display: flex; gap: 10px; }

.profile-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}
.profile-tabs button {
  padding: 10px 25px;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  color: #888;
  cursor: pointer;
  font-size: 1rem;
  margin-bottom: -2px;
}
.profile-tabs button.active {
  color: #00d4ff;
  border-bottom-color: #00d4ff;
}

.app-footer {
  text-align: center;
  padding: 30px;
  color: #555;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: 50px;
}
</style>