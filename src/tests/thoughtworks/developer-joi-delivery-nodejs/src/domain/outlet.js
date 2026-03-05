class Outlet {
  /**
   * @param {string} name
   * @param {string} description
   * @param {string} outletId
   */
  constructor(name, description, outletId) {
    /** @type {string} */
    this.name = name;

    /** @type {string} */
    this.description = description;

    /** @type {string} */
    this.outletId = outletId;
  }
}

module.exports = Outlet;
