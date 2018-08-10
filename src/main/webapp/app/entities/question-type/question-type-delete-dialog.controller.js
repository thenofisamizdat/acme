(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionTypeDeleteController',QuestionTypeDeleteController);

    QuestionTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'QuestionType'];

    function QuestionTypeDeleteController($uibModalInstance, entity, QuestionType) {
        var vm = this;

        vm.questionType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            QuestionType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
