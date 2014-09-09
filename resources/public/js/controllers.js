'use strict';

angular.module("shl").controller('shlCtrl', ['$scope', 'GetActiveTournaments', 'GetConferences', 'GetUser', 'GetGames', 'UpdateGame', 
  function ($scope, GetActiveTournaments, GetConferences, GetUser, GetGames, UpdateGame) {

  $scope.Read = true;
  $scope.logged = false;

  $scope.login = function()
  {
    getUser(this.username, function(user){
      $scope.user = user;
    });
    $scope.logged = true;
    getTournament(function(tournament){
      $scope.tournament = tournament;
      getConferences(tournament.id, function(conferences){
      $scope.conferences = conferences;
    });
    });
  }



  $scope.IsLogin = function()
  {
    if($scope.logged)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  $scope.LogOut = function()
  {
    $scope.logged = false;
    $scope.username = "";
  }


  $scope.changeToInput = function(id)
  {
    $scope.EditItemId = id;
  }

  $scope.Update = function(row)
  {
    $scope.EditItemId = false;
    updateGame(row, function(callback)
    {
      getGames($scope.conferenceid, function(games){
      $scope.schedule = games;
    });
    });
  }

  $scope.Read = function(id)
  {
    if($scope.EditItemId === id)
      return false;
    else
    {
      return true;
    }
  }

  $scope.sarjaohjelma = function(conferenceid){
    $scope.showWrapper = true;
    $scope.showSchedule = true;
    $scope.showStandings = false;
    $scope.EditItemId  = false;
    getGames(conferenceid, function(games){
      $scope.schedule = games;
    });

}


  $scope.sarjataulukko = function(conferenceid, conferenceName) {
    $scope.showWrapper = true;
    $scope.showSchedule = false;
    $scope.showStandings = true;
    $scope.conferenceid = conferenceid;
    $scope.conferenceName = conferenceName;
    $scope.EditItemId  = false;
  }

    var getTournament = function(callback){
      return GetActiveTournaments.get({
        }, callback);
    };

    var getConferences = function(TournamentId, callback){
      return GetConferences.get({
        id : TournamentId
        }, callback);
    };

    var getUser = function(UserName, callback){
      return GetUser.get({
        username : UserName
        }, callback);
    };

    var getGames = function(ConferenceId, callback){
      return GetGames.get({
        conferenceid : ConferenceId
        }, callback);
    };

    var updateGame = function(row,callback) {
       return UpdateGame.save({
        id:row.id,
        homegoals: row.homegoals,
        awaygoals: row.awaygoals,
        overtime:row.overtime,
        shootout:row.shootout,
        playdate:row.playdate
       }, callback);
    };

}]);




