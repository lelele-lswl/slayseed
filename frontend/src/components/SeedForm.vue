<template>
  <div class='seed-form-container'>
    <button @click='showForm = !showForm' class='toggle-btn'>
      {{ showForm ? '收起表单' : '+ 分享新种子' }}
    </button>
    
    <form v-if='showForm' @submit.prevent='submitSeed' class='seed-form'>
      <div class='form-group'>
        <label>种子码</label>
        <input v-model='seed.seedCode' required placeholder='输入种子码' />
      </div>
      <div class='form-group'>
        <label>角色</label>
        <select v-model='seed.character' required>
          <option value=''>选择角色</option>
          <option v-for='char in characters' :key='char' :value='char'>{{ char }}</option>
        </select>
      </div>
      <div class='form-group'>
        <label>描述</label>
        <textarea v-model='seed.description' required placeholder='描述这个种子的特点和游戏版本...' rows='3'></textarea>
      </div>
      <div class='form-group'>
        <label>标签（用逗号分隔）</label>
        <input v-model='seed.tags' placeholder='例如：好开局,多遗物,简单' />
      </div>
      <button type='submit' class='submit-btn'>提交分享</button>
    </form>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue'
import axios from 'axios'

const emit = defineEmits(['seed-added'])

const showForm = ref(false)
const characters = ['铁甲战士', '静默猎手', '故障机器人', '观者']

const seed = ref({
  seedCode: '',
  character: '',
  description: '',
  tags: ''
})

const submitSeed = async () => {
  try {
    await axios.post('/api/seeds', seed.value)
    seed.value = { seedCode: '', character: '', description: '', tags: '' }
    showForm.value = false
    emit('seed-added')
    alert('种子分享成功！')
  } catch (error) {
    console.error('提交失败:', error)
    alert('提交失败，请稍后重试')
  }
}
</script>

<style scoped>
.seed-form-container {
  margin-bottom: 30px;
  text-align: center;
}

.toggle-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border: none;
  border-radius: 25px;
  color: white;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.toggle-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(231, 76, 60, 0.5);
}

.seed-form {
  margin-top: 20px;
  background: linear-gradient(145deg, #2a2a4a, #1e1e3a);
  padding: 30px;
  border-radius: 15px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #00d4ff;
  font-weight: bold;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 1rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #00d4ff;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 0 20px rgba(39, 174, 96, 0.5);
}
</style>