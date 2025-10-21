<template>
  <el-select
    v-model="selectedValues"
    :placeholder="placeholder"
    :multiple="multiple"
    :clearable="clearable"
    filterable
    :filter-method="handleLocalFilter"
    @change="handleChange"
    :style="{ width: width }"
    size="small"
    :loading="initLoading"
  >
    <el-option
      v-for="person in personList"
      :key="person.userId"
      :label="person.nickName"
      :value="valueKey === 'id' ? person.userId : person.nickName"
    />
    <!-- 无匹配结果提示 -->
    <el-option
      v-if="personList.length === 0 && !initLoading"
      value=""
      disabled
    >
      暂无匹配人员
    </el-option>
  </el-select>
</template>

<script>
import { initView } from "@/api/system/user";

export default {
  name: "PersonPicker",
  props: {
    multiple: {
      type: Boolean,
      default: false
    },
    value: {
      type: [Number, String, Array],
      // 修复：默认值用普通函数，避免箭头函数this指向问题
      default() {
        return this.multiple ? [] : null;
      }
    },
    valueKey: {
      type: String,
      default: 'id',
      validator: (value) => ['id', 'name'].includes(value)
    },
    placeholder: {
      type: String,
      default: '请选择人员'
    },
    width: {
      type: String,
      default: '100%'
    },
    clearable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      // 全部人员数据（初始化加载）
      allPersonList: [],
      // 过滤后显示的人员数据
      personList: [],
      // 初始化加载状态
      initLoading: false,
      selectedValues: this.value
    };
  },
  watch: {
    value(newVal) {
      this.selectedValues = newVal;
    }
  },
  created() {
    // 关键修复：初始化组件构造函数的静态属性（避免undefined）
    this.initStaticProps();
    // 加载人员数据
    this.loadPersons();
  },
  methods: {
    // 初始化静态属性（缓存、加载状态、等待队列）
    initStaticProps() {
      const Constructor = this.constructor;
      // 若静态属性未定义，则初始化
      if (!Constructor.personCache) {
        Constructor.personCache = null; // 缓存人员数据
      }
      if (Constructor.isLoading === undefined) {
        Constructor.isLoading = false; // 是否正在加载
      }
      if (!Constructor.resolveQueue) {
        Constructor.resolveQueue = []; // 等待队列（存储回调）
      }
    },

    // 加载人员数据（带缓存和防重复请求处理）
    loadPersons() {
      const Constructor = this.constructor;

      // 1. 有缓存直接用
      if (Constructor.personCache) {
        this.allPersonList = Constructor.personCache;
        this.personList = [...this.allPersonList];
        return;
      }

      // 2. 正在加载中，加入等待队列
      if (Constructor.isLoading) {
        // 此时resolveQueue已初始化，可安全push
        Constructor.resolveQueue.push(() => {
          this.allPersonList = Constructor.personCache;
          this.personList = [...this.allPersonList];
        });
        return;
      }

      // 3. 发起新请求
      this.initLoading = true;
      Constructor.isLoading = true;

      // 调用接口（pageSize扩大到1000避免数据不全）
      initView({ pageSize: 1000 }).then(response => {
        if (response.code === 200) {
          Constructor.personCache = response.rows || [];
          this.allPersonList = Constructor.personCache;
          this.personList = [...this.allPersonList];
        } else {
          this.$message.error(response.msg || '获取人员列表失败');
          this.allPersonList = [];
          this.personList = [];
        }
        this.finishLoading();
      }).catch(error => {
        console.error('获取人员失败：', error);
        this.$message.error('获取人员列表失败');
        this.allPersonList = [];
        this.personList = [];
        this.finishLoading();
      });
    },

    // 完成加载处理（重置状态+执行等待队列）
    finishLoading() {
      const Constructor = this.constructor;
      this.initLoading = false;
      Constructor.isLoading = false;

      // 安全处理：确保resolveQueue是数组再遍历
      if (Array.isArray(Constructor.resolveQueue)) {
        // 执行所有等待的回调
        while (Constructor.resolveQueue.length > 0) {
          const callback = Constructor.resolveQueue.shift();
          // 防止回调执行报错影响后续
          try {
            callback();
          } catch (e) {
            console.error('执行等待队列回调失败：', e);
          }
        }
      }
    },

    // 纯前端模糊匹配过滤
    handleLocalFilter(query) {
      const searchStr = query.trim().toLowerCase();
      // 无查询条件时显示全部人员
      if (!searchStr) {
        this.personList = [...this.allPersonList];
        return;
      }
      // 前端模糊匹配：人员名称包含查询字符串（不区分大小写）
      this.personList = this.allPersonList.filter(person => {
        // 安全处理：确保person.nickName存在
        return person?.nickName?.toLowerCase().includes(searchStr) || false;
      });
    },

    // 选择变化回调
    handleChange(val) {
      this.$emit('input', val);
      this.$emit('change', val);

      let selectedPersons = [];
      const Constructor = this.constructor;
      const allPersons = Constructor.personCache || [];

      if (this.multiple) {
        selectedPersons = allPersons.filter(person => {
          const personValue = this.valueKey === 'id' ? person.userId : person.nickName;
          return val.includes(personValue);
        });
      } else {
        selectedPersons = allPersons.find(person => {
          return this.valueKey === 'id' ? person.userId === val : person.nickName === val;
        }) || null;
      }

      this.$emit('select', selectedPersons);
    }
  }
};
</script>
