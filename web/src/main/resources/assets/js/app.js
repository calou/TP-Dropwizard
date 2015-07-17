var kanbanApp = angular.module('kanbanApp', ['ngResource']);
kanbanApp.factory('Project', ['$resource',
    function($resource){
        return $resource('/api/projects/:projectId', {projectId:'@id'}
        );
    }
]);

kanbanApp.controller('projectController', function($scope, Project) {
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
