(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionnaireDeleteController',QuestionnaireDeleteController);

    QuestionnaireDeleteController.$inject = ['$uibModalInstance', 'entity', 'Questionnaire'];

    function QuestionnaireDeleteController($uibModalInstance, entity, Questionnaire) {
        var vm = this;

        vm.questionnaire = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Questionnaire.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
