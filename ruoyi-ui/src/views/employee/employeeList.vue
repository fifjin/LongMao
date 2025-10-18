<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" ref="queryForm" label-width="68px">
        <el-form-item label="用户名称" prop="userName">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名称"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input
            v-model="queryParams.phonenumber"
            placeholder="请输入手机号码"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="闲忙状态" prop="state">
          <el-select
            v-model="queryParams.state"
            placeholder="请选择闲忙状态"
            clearable
            size="small"
            style="width: 200px"
          >
            <el-option label="可接单" value="0" />
            <el-option label="忙碌中" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            size="small"
            style="width: 240px"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
          <el-button type="primary" icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="employeeList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
    >
      <el-table-column label="用户ID" prop="userId" align="center" width="80" />
      <el-table-column label="用户昵称" prop="nickName" align="center" />
      <el-table-column label="部门名称" prop="deptName" align="center" />
      <el-table-column label="手机号码" prop="phonenumber" align="center" />
      <el-table-column label="闲忙状态" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.state"
            active-value="1"
            inactive-value="0"
            active-text="忙碌中"
            inactive-text="可接单"
            @change="handleStateChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="已接手订单数" prop="receivedOrderCount" align="center" />
      <el-table-column label="已完成订单数" prop="completedOrderCount" align="center" />
      <el-table-column label="异常单数" prop="abnormalOrderCount" align="center" />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listEmployee, exportEmployee, changeEmployeeState } from "@/api/system/employee";
import Pagination from "@/components/Pagination";

export default {
  name: "EmployeeList",
  components: { Pagination },
  data() {
    return {
      // 加载状态
      loading: false,
      // 总条数
      total: 0,
      // 打手列表
      employeeList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: "",
        phonenumber: "",
        state: "",
        beginTime: "",
        endTime: ""
      },
      // 日期范围
      dateRange: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取列表
    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length > 0) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      } else {
        this.queryParams.beginTime = "";
        this.queryParams.endTime = "";
      }
      listEmployee(this.queryParams).then(response => {
        this.employeeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.dateRange = [];
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 导出
    handleExport() {
      exportEmployee(this.queryParams).then(response => {
        this.download(response);
      });
    },
    // 修改状态
    handleStateChange(row) {
      const data = {
        userId: row.userId,
        state: row.state
      };
      changeEmployeeState(data).then(response => {
        this.$modal.msgSuccess("修改成功");
        this.getList();
      }).catch(() => {
        row.state = row.state === "0" ? "1" : "0"; // 失败回滚
      });
    }
  }
};
</script>

<style scoped>
.search-box {
  background: #f5f7fa;
  padding: 10px 20px;
  border-radius: 4px;
}
</style>
