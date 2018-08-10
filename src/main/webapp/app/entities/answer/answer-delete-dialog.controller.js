(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerDeleteController',AnswerDeleteController);

    AnswerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Answer'];

    function AnswerDeleteController($uibModalInstance, entity, Answer) {
        var vm = this;

        vm.answer = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Answer.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
