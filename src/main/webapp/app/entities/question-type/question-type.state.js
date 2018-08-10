(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('question-type', {
            parent: 'entity',
            url: '/question-type',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'QuestionTypes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/question-type/question-types.html',
                    controller: 'QuestionTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('question-type-detail', {
            parent: 'question-type',
            url: '/question-type/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'QuestionType'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/question-type/question-type-detail.html',
                    controller: 'QuestionTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'QuestionType', function($stateParams, QuestionType) {
                    return QuestionType.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'question-type',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('question-type-detail.edit', {
            parent: 'question-type-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question-type/question-type-dialog.html',
                    controller: 'QuestionTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QuestionType', function(QuestionType) {
                            return QuestionType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('question-type.new', {
            parent: 'question-type',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question-type/question-type-dialog.html',
                    controller: 'QuestionTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                type: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('question-type', null, { reload: 'question-type' });
                }, function() {
                    $state.go('question-type');
                });
            }]
        })
        .state('question-type.edit', {
            parent: 'question-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question-type/question-type-dialog.html',
                    controller: 'QuestionTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['QuestionType', function(QuestionType) {
                            return QuestionType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('question-type', null, { reload: 'question-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('question-type.delete', {
            parent: 'question-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/question-type/question-type-delete-dialog.html',
                    controller: 'QuestionTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['QuestionType', function(QuestionType) {
                            return QuestionType.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('question-type', null, { reload: 'question-type' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
