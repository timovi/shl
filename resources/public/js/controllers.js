'use strict';

angular.module("shl").controller('shlCtrl', ['$scope', 'GetActiveTournaments', 'GetConferences', 'GetUser', 
  function ($scope, GetActiveTournaments, GetConferences, GetUser) {

  $scope.Read = true;
  $scope.logged = false;

  $scope.login = function()
  {
    //console.log("Kirjautunut:" + this.username);
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

  $scope.Save = function(id, homegoal, awaygoal)
  {
    $scope.EditItemId = false;
    console.log("Tallennettu:" + id);
    console.log("Koti:" + homegoal);
    console.log("Vieras:" + awaygoal);
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

  $scope.sarjaohjelma = function(id){
    $scope.showWrapper = true;
    $scope.showSchedule = true;
    $scope.showStandings = false;
    if(id === 0){
      $scope.id= id;
      $scope.scheduleHeader = "Pohjoinen Konferenssi -sarjaohjelma"   
      $scope.schedule = [
      {"id": "1",
       "date": "12.6.2013",
       "hometeam": "Capitals",
       "awayteam": "Hawks",
       "homegoal": "3",
       "awaygoal": "2"
        },
      {"id": "2",
       "date": "13.6.2013",
       "hometeam": "LA",
       "awayteam": "Flyers",
       "homegoal": "6",
       "awaygoal": "5"}
    ];
  }

  if(id === 1){
      $scope.id= id;
      $scope.scheduleHeader = "Eteläinen Konferenssi -sarjaohjelma"
      $scope.schedule = [
      {"id": "1",
       "date": "12.6.2013",
       "hometeam": "Sharks",
       "awayteam": "Pens",
       "homegoal": "3",
       "awaygoal": "2"
        },
      {"id": "2",
       "date": "13.6.2013",
       "hometeam": "Canadiens",
       "awayteam": "Bruins",
       "homegoal": "6",
       "awaygoal": "5"}
    ];
  }
  if(id === 2){
      $scope.scheduleHeader = "Oma -sarjaohjelma"
      $scope.schedule = [
      {"id": "5",
       "date": "10.10.2013",
       "hometeam": "Hokki",
       "awayteam": "Ilves",
       "homegoal": "3",
       "awaygoal": "2"
        },
      {"id": "6",
       "date": "12.10.2013",
       "hometeam": "Tappara",
       "awayteam": "Hokki",
       "homegoal": "2",
       "awaygoal": "5"}
    ];
  }
}


  $scope.sarjataulukko = function(id, conferenceName) {
    $scope.showWrapper = true;
    $scope.showSchedule = false;
    $scope.showStandings = true;
    $scope.id= id;
    if(id === 3){
    	$scope.conferenceName = conferenceName;
    	$scope.teams = [
      {"name": "Capitals",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"},
      {"name": "Hawks",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"},
      {"name": "LA",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"}
    ];
  }

  /*if(id === 1){
    	$scope.conference = "Eteläinen Konferenssi"
    	$scope.teams = [
      {"name": "Sharks",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"},
      {"name": "Pens",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"},
      {"name": "Canadiens",
       "games": "3",
       "goals": "10",
       "againtsGoals": "3",
       "points": "12"}
    ];
  }*/

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
  
}]);




