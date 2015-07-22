var kanbanApp = angular.module('kanbanApp', ['ngResource', 'ngRoute', 'ngAnimate']);

kanbanApp.factory('Project', ['$resource',
    function($resource){
            return $resource('/api/projects/:projectId', {projectId:'@id'},
                {'update': { method:'PUT' }}
        );
    }
]);

kanbanApp.factory('Story', ['$resource',
    function($resource){
            return $resource('/api/stories/:storyId', {storyId:'@id'},
                {'update': { method:'PUT' }}
        );
    }
]);

kanbanApp.controller('ProjectListController', function($scope, Project) {
    $scope.projectList = Project.query();

    $scope.title = '';
    $scope.description = '';

    $scope.createProject = function(){
        if(true){
            var project = new Project();
            project.title = $scope.title;
            project.description = $scope.description;
            project.$save();
            $scope.projectList.push(project);
            $scope.title = '';
            $scope.description = '';
        }
    };

    $scope.delete = function(project){
        project.$delete().then(function(project){
            $scope.projectList = Project.query();
        });
    };
});

kanbanApp.controller('ProjectDetailsController', function($scope, $routeParams, Project, Story) {
    $scope.projectId = $routeParams.projectId;
    $scope.project = Project.get({projectId:$scope.projectId});
    $scope.storyForDetails = {}
    function resetNewForm(){
        $scope.title = '';
        $scope.description = '';
        $scope.priority = 2;
        $scope.bgColor="#E9E74A";
        $scope.fgColor="#CCCCCC";
        $scope.points = 1;
    }
    resetNewForm();
    $scope.createStory = function(){
        if(true){
            var story = new Story();
            story.title = $scope.title;
            story.project = {id: $scope.projectId};
            story.description = $scope.description;
            story.priority = $scope.priority;
            story.bgColor = $scope.bgColor;
            story.fgColor = $scope.fgColor;
            story.points = $scope.points;
            story.$save().then(function(res)  {
                $scope.project = Project.get({projectId:$scope.projectId});
                resetNewForm();
            });
        }
    };
    $scope.move = function(s){
        s.moving = true;
        var nextStep;
        var storyStep = s.step;
        if(storyStep == undefined){
            nextStep = $scope.project.steps[0];
        }else{
            // On détermine le premier step dont l'ordre est supérieur à storyStep
            for(var i in $scope.project.steps){
                var si = $scope.project.steps[i];

                // Si l'ordre du step n'est pas définie...
                if(storyStep.order == undefined && storyStep.id == si.id){
                    storyStep.order = si.order;
                }
                // On choisit le premier step dont l'ordre est plus grand que
                // celui du step courant.
                if(storyStep.order < si.order){
                    nextStep = si;
                    break;
                }
            }
        }

        if(nextStep != undefined && nextStep != storyStep){
            s.step = nextStep;
            var story = new Story(s);
            story.$update().then(function(){
                s.step = nextStep;
                s.moving = false;
            });
        }
    };

    $scope.moveBackward = function(s){
        var prevStep;
        var storyStep = s.step;
        s.moving = true;
        // On détermine le premier step dont l'ordre est supérieur à storyStep
        for(var i in $scope.project.steps){
            // Parcours depuis le dernier élément
            var si = $scope.project.steps[$scope.project.steps.length - i - 1];

            // Si l'ordre du step n'est pas définie...
            if(storyStep.order == undefined && storyStep.id == si.id){
                storyStep.order = si.order;
            }
            // On choisit le premier step dont l'ordre est plus grand que
            // celui du step courant.
            if(storyStep.order > si.order){
                prevStep = si;
            }
        }
        if(!prevStep){
            prevStep = null;
        }

        s.step = prevStep;
        var story = new Story(s);

        story.$update().then(function(){
            s.step = prevStep;
            s.moving = false;
        });
    };

    $scope.showDetails = function(story){
        $scope.storyForDetails = story;
        console.log(story);
    };
});


kanbanApp.directive('postit', function() {
    return {
        restrict: 'AE',
        replace: 'true',
        templateUrl: 'directives/post-it.html',
        scope: {
            story: "=",
            onDetails: '&',
            onMove: '&',
            onMoveBackward: '&'
        }
    };
});

kanbanApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/projects', {
        templateUrl: 'partials/project-list.html',
        controller: 'ProjectListController'
      }).
      when('/projects/:projectId', {
        templateUrl: 'partials/project-details.html',
        controller: 'ProjectDetailsController'
      }).
      otherwise({
        redirectTo: '/projects'
      });
  }
]);