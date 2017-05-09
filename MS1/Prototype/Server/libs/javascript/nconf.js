
/**
 * Created by Pastuh on 09.05.2017.
 */
var nconf = require('nconf');

nconf.argv()
    .env()
    .file({ file: '../config.json' });

module.exports = nconf;
