'use strict'

var host = 'http://localhost:8080'

angular.module('shlServices', ['ngResource'])
	.factory('GetActiveTournaments', ['$resource', function ($resource) {
        return $resource(host + '/tournaments/active', {}, {});
    }]);