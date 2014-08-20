'use strict'

var host = 'http://localhost:8080'

angular.module('shlServices', ['ngResource'])
	.factory('GetActiveTournaments', ['$resource', function ($resource) {
        return $resource(host + '/tournaments/active/ ', {}, {});
    }])
    .factory('GetConferences', ['$resource', function ($resource) {
        return $resource(host + '/tournaments/:id/conferences/ ', {}, { 'get': {method: 'GET', isArray: true }});
    }])
    .factory('GetUser', ['$resource', function ($resource) {
        return $resource(host + '/users/:username/ ', {}, { 'get': {method: 'GET', isArray: false }});
    }]);