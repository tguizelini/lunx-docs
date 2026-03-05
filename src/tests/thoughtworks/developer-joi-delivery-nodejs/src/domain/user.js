class User {
  /**
   * @param {string} userId
   * @param {string} username
   * @param {string} firstName
   * @param {string} lastName
   * @param {string} email
   * @param {string} phoneNumber
   * @param {Cart} cart
   */

  constructor(userId, username, firstName, lastName, email, phoneNumber, cart) {
    /** @type {string} */
    this.userId = userId;
    /** @type {string} */
    this.username = username;
    /** @type {string} */
    this.firstName = firstName;
    /** @type {string} */
    this.lastName = lastName;
    /** @type {string} */
    this.email = email;
    /** @type {string} */
    this.phoneNumber = phoneNumber;

    /** @type {Cart} */
    this.cart = cart;
  }
}

module.exports = User;
