(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnsweredQuestionnaireDeleteController',AnsweredQuestionnaireDeleteController);

    AnsweredQuestionnaireDeleteController.$inject = ['$uibModalInstance', 'entity', 'AnsweredQuestionnaire'];

    function AnsweredQuestionnaireDeleteController($uibModalInstance, entity, AnsweredQuestionnaire) {
        var vm = this;

        vm.answeredQuestionnaire = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AnsweredQuestionnaire.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
