import { api, API_CONSTANTS } from 'src/boot/axios'

class UserService {
  getAllUsers() {
    return api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}`)
  }

  getUserById(userId) {
    return api.get(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.EMPLOYEE_ROUTE}/${userId}`)
  }

  updateUser(userId, userData) {
    return api.put(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}/${userId}`, userData)
  }

  deleteUser(userId) {
    return api.delete(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}/${userId}`)
  }

  updateUserRole(userId, role) {
    return api.put(
      `${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}/${userId}/role?role=${encodeURIComponent(role)}`,
    )
  }

  // Añadir método para crear usuario si no existe
  createUser(userData) {
    return api.post(`${API_CONSTANTS.V1_ROUTE}${API_CONSTANTS.USERS_ROUTE}`, userData)
  }
}

export default new UserService()
