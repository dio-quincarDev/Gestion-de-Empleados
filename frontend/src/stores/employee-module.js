import { defineStore } from 'pinia';
import EmployeeService from 'src/service/employee.service.js';

export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: [],
    selectedEmployee: null,
    loading: false,
    error: null,
  }),

  getters: {
    getAllEmployees: (state) => state.employees,
    getEmployeeById: (state) => state.selectedEmployee,
  },

  actions: {
    async fetchEmployees() {
      this.loading = true;
      this.error = null;
      try {
        this.employees = await EmployeeService.getEmployees();
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },

    async fetchEmployeeById(id) {
      this.loading = true;
      this.error = null;
      this.selectedEmployee = null;
      try {
        this.selectedEmployee = await EmployeeService.getById(id);
      } catch (error) {
        this.error = error;
      } finally {
        this.loading = false;
      }
    },

    async createEmployee(employeeData) {
      this.loading = true;
      this.error = null;
      try {
        await EmployeeService.createEmployee(employeeData);
        await this.fetchEmployees();
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async updateEmployee(employeeData) {
      this.loading = true;
      this.error = null;
      try {
        await EmployeeService.updateEmployee(employeeData.id, employeeData);
        await this.fetchEmployees();
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async deleteEmployee(employeeId) {
      this.loading = true;
      this.error = null;
      try {
        await EmployeeService.deleteEmployee(employeeId);
        await this.fetchEmployees();
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
});

