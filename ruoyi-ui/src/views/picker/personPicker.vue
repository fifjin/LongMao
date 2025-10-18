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
      default: () => this.multiple ? [] : null
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
    // 组件初始化时加载全部人员数据
    this.loadAllPersons();
  },
  methods: {
    // 加载全部人员数据
    loadAllPersons() {
      this.initLoading = true;
      // 调用接口加载所有人员（设置足够大的pageSize）
      initView({ pageSize: 100 }).then(response => {
        if (response.code === 200) {
          this.allPersonList = response.rows || [];
          this.personList = [...this.allPersonList]; // 初始显示全部
        } else {
          this.$message.error(response.msg || '获取人员列表失败');
          this.allPersonList = [];
          this.personList = [];
        }
        this.initLoading = false;
      }).catch(error => {
        console.error('获取人员失败：', error);
        this.$message.error('获取人员列表失败');
        this.allPersonList = [];
        this.personList = [];
        this.initLoading = false;
      });
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
        return person.nickName.toLowerCase().includes(searchStr);
      });
    },

    // 选择变化回调
    handleChange(val) {
      this.$emit('input', val);
      this.$emit('change', val);

      let selectedPersons = [];
      if (this.multiple) {
        selectedPersons = this.allPersonList.filter(person => {
          const personValue = this.valueKey === 'id' ? person.userId : person.nickName;
          return val.includes(personValue);
        });
      } else {
        selectedPersons = this.allPersonList.find(person => {
          return this.valueKey === 'id' ? person.userId === val : person.nickName === val;
        }) || null;
      }
      this.$emit('select', selectedPersons);
    }
  }
};
</script>
