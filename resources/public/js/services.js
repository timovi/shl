'use strict'

var host = 'http://localhost:8080/shl'

angular.module('shlServices', ['ngResource'])
	.factory('GetActiveTournaments', ['$resource', function ($resource) {
        return $resource(host + '/tournaments/active/ ', {}, {});
    }])
    .factory('GetConferences', ['$resource', function ($resource) {
        return $resource(host + '/tournaments/:id/conferences/ ', {}, { 'get': {method: 'GET', isArray: true }});
    }])
    .factory('GetUser', ['$resource', function ($resource) {
        return $resource(host + '/users/:username/ ', {}, { 'get': {method: 'GET', isArray: false}});
    }])
    .factory('GetGames', ['$resource', function ($resource) {
        return $resource(host + '/games/conference/:conferenceid/ ', {}, { 'get': {method: 'GET', isArray: true}});
    }])
    .factory('GetOwnGames', ['$resource', function ($resource) {
        return $resource(host + '/games/player/:playerid/ ', {}, { 'get': {method: 'GET', isArray: true}});
    }])
    .factory('GetConferenceStandings', ['$resource', function ($resource) {
        return $resource(host + '/standings/conference/:conferenceid/ ', {}, { 'get': {method: 'GET', isArray: true}});
    }])

    .factory('UpdateGame', ['$resource', function ($resource) {
        return $resource(host + '/games ', {}, { 'post': {method: 'POST', isArray: false}});
    }]);