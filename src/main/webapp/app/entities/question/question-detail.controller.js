(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionDetailController', QuestionDetailController);

    QuestionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Question', 'QuestionType', 'Questionnaire'];

    function QuestionDetailController($scope, $rootScope, $stateParams, previousState, entity, Question, QuestionType, Questionnaire) {
        var vm = this;

        vm.question = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('acmeApp:questionUpdate', function(event, result) {
            vm.question = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
