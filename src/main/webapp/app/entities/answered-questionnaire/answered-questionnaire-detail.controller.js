(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnsweredQuestionnaireDetailController', AnsweredQuestionnaireDetailController);

    AnsweredQuestionnaireDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'AnsweredQuestionnaire', 'Answer'];

    function AnsweredQuestionnaireDetailController($scope, $rootScope, $stateParams, previousState, entity, AnsweredQuestionnaire, Answer) {
        var vm = this;

        vm.answeredQuestionnaire = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('acmeApp:answeredQuestionnaireUpdate', function(event, result) {
            vm.answeredQuestionnaire = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
