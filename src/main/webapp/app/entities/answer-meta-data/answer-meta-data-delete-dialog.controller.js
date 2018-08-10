(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerMetaDataDeleteController',AnswerMetaDataDeleteController);

    AnswerMetaDataDeleteController.$inject = ['$uibModalInstance', 'entity', 'AnswerMetaData'];

    function AnswerMetaDataDeleteController($uibModalInstance, entity, AnswerMetaData) {
        var vm = this;

        vm.answerMetaData = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AnswerMetaData.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
