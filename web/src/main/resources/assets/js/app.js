var kanbanApp = angular.module('kanbanApp', ['ngResource', 'ngRoute']);

kanbanApp.factory('Project', ['$resource',
    function($resource){
            return $resource('/api/projects/:projectId', {projectId:'@id'}
        );
    }
]);

kanbanApp.factory('Story', ['$resource',
    function($resource){
            return $resource('/api/stories/:storyId', {storyId:'@id'}
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

    $scope.title = '';
    $scope.description = '';
    $scope.createStory = function(){
        if(true){
            var story = new Story();
            story.title = $scope.title;
            story.project = {id: $scope.projectId};
            story.description = $scope.description;
            story.$save().then(function(res)  {
                $scope.project = Project.get({projectId:$scope.projectId});
                $scope.title = '';
                $scope.description = '';
            });

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