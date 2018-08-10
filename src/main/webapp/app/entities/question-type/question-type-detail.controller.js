(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionTypeDetailController', QuestionTypeDetailController);

    QuestionTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'QuestionType', 'Question'];

    function QuestionTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, QuestionType, Question) {
        var vm = this;

        vm.questionType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('acmeApp:questionTypeUpdate', function(event, result) {
            vm.questionType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
