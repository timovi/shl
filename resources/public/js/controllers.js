'use strict';


angular.module("shl").controller('shlCtrl', ['$scope', 'GetActiveTournaments', 'GetConferences', 'GetConferenceStandings', 'GetUser', 'GetGames', 'UpdateGame', '$filter',
  'GetOwnGames', 
  function ($scope, GetActiveTournaments, GetConferences, GetConferenceStandings, GetUser, GetGames, UpdateGame, $filter, GetOwnGames) {

  $scope.Read = true;
  $scope.logged = false;

  $scope.init = function()
  {
    getTournament(function(tournament){
      $scope.tournament = tournament;
      getConferences(tournament.id, function(conferences){
      $scope.conferences = conferences;
    });
    });
  }


  $scope.login = function()
  {
    getUser(this.username, function(user){
      $scope.user = user;
      if(user.role !== undefined)
      {
        $scope.logged = true;
      }
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


  $scope.changeToInput = function(id, row)
  {
    $scope.playdate = new Date();
    $scope.EditItemId = id;
    if(row.playdate)
    {
      $scope.playdate = row.playdate;
    }
    $scope.playdate = $filter('date')($scope.playdate, "dd.MM.yyyy");  // for type="date" binding
  }

  $scope.Update = function(row, playdate)
  {
    $scope.EditItemId = false;
    updateGame(row, playdate, function(callback)
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

  $scope.sarjaohjelma = function(conferenceid, conferencename){
    $scope.showWrapper = true;
    $scope.showSchedule = true;
    $scope.showStandings = false;
    $scope.EditItemId  = false;
    $scope.conferenceName = conferencename;
    getGames(conferenceid, function(games){
      $scope.schedule = games;

    });

}
  $scope.omasarjaohjelma = function(userid){
    $scope.showWrapper = true;
    $scope.showSchedule = true;
    $scope.showStandings = false;
    $scope.EditItemId  = false;
    $scope.conferenceName = "Oma sarjaohjelma"
     getOwnGames(userid, function(games){
      $scope.schedule = games;
    });
  }


  $scope.sarjataulukko = function(conferenceid, conferencename) {
    $scope.showWrapper = true;
    $scope.showSchedule = false;
    $scope.showStandings = true;
    $scope.conferenceid = conferenceid;
    $scope.conferenceName = conferencename;
    $scope.activeconference = conferencename;
    $scope.EditItemId  = false;
    getConferenceStandings(conferenceid, function(standings){
      $scope.standings = standings;

    });
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

    var getConferenceStandings = function(ConferenceId, callback){
      return GetConferenceStandings.get({
        conferenceid : ConferenceId
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

    var getOwnGames = function(userId, callback){
      return GetOwnGames.get({
        playerid : userId
        }, callback);
    };

    var updateGame = function(row, playdate, callback) {
       return UpdateGame.save({
        id:row.id,
        homegoals: row.homegoals,
        awaygoals: row.awaygoals,
        overtime:row.overtime,
        shootout:row.shootout,
        playdate:playdate
       }, callback);
    };

}]);

//Filters

angular.module("shl").filter('removeSpacesThenLowercase', function () {
        return function (text) {
        var str = text.replace(/\s+/g, '-');
        return str.toLowerCase();
        };
})





